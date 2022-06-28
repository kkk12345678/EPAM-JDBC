package org.example.epam.jdbc.service;


import org.example.epam.jdbc.entity.FilesEntry;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;


public class FilesService implements Closeable {
    private static final String TABLE_NAME = "files";

    private final static String CREATE_QUERY =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " (id INT AUTO_INCREMENT PRIMARY KEY," +
            "name VARCHAR(255) NOT NULL," +
            "parent_id INT," +
            "is_directory TINYINT(1) NOT NULL," +
            "disc_usage BIGINT) ENGINE = INNODB;";
    private final static String DROP_QUERY =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
    private final static String TRUNCATE_QUERY =
            "TRUNCATE " + TABLE_NAME;
    private final static String INSERT_QUERY =
            "INSERT INTO " + TABLE_NAME + " VALUES (null, ?, ?, ?, ?)";
    private final static String UPDATE_QUERY =
            "UPDATE " + TABLE_NAME + " SET name = ?, parent_id = ?, is_directory = ?, disc_usage = ? WHERE id = ?";
    private final static String DELETE_QUERY =
            "DELETE FROM files WHERE id = ?";
    private final static String SELECT_QUERY =
            "SELECT id, name, parent_id, is_directory, disc_usage FROM " + TABLE_NAME;
    private final static String COUNT_QUERY =
            "SELECT COUNT(*) FROM " + TABLE_NAME;


    private final Connection connection;
    public FilesService() {
        try (FileInputStream fileInputStream = new FileInputStream("D:\\mdocs\\programming\\Projects\\java\\IntelliJ IDEA\\Epam\\JDBC\\src\\main\\resources\\database.properties")) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            Class.forName(properties.getProperty("db.driver"));
            this.connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password"));
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_QUERY);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean truncateTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(TRUNCATE_QUERY);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Integer insert(String name, Integer parentId, boolean isDirectory, Long discUsage) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            if (parentId != null) {
                statement.setInt(2, parentId);
            } else {
                statement.setNull(2, Types.INTEGER);
            }
            statement.setInt(3, isDirectory ? 1 : 0);
            if (discUsage != null) {
                statement.setLong(4, discUsage);
            } else {
                statement.setNull(4, Types.BIGINT);
            }
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean dropTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(DROP_QUERY);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Integer count() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(COUNT_QUERY);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public FilesEntry update(FilesEntry newFilesEntry) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            int id = newFilesEntry.getId();
            FilesEntry oldFilesEntry = selectOne(id);
            statement.setString(1, newFilesEntry.getName());
            Integer parentId = newFilesEntry.getParentId();
            if (parentId != null) {
                statement.setInt(2, parentId);
            } else {
                statement.setNull(2, Types.INTEGER);
            }
            statement.setInt(3, newFilesEntry.isDirectory() ? 1 : 0);
            Long discUsage = newFilesEntry.getDiscUsage();
            if (discUsage != null) {
                statement.setLong(4, discUsage);
            } else {
                statement.setNull(4, Types.BIGINT);
            }
            statement.setInt(5, id);
            statement.execute();
            return oldFilesEntry;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void delete(int id) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FilesEntry[] selectAll() {
        FilesEntry[] result = new FilesEntry[count()];
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SELECT_QUERY);
            int i = 0;
            while(resultSet.next()) {
                result[i++] = new FilesEntry(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4) == 1,
                        resultSet.getLong(5)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public FilesEntry[] selectAll(int parent_id) {
        int size = 0;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(COUNT_QUERY + " WHERE parent_id = " + parent_id);
            resultSet.next();
            size = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        FilesEntry[] result = new FilesEntry[size];
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SELECT_QUERY + " WHERE parent_id = " + parent_id);
            int i = 0;
            while(resultSet.next()) {
                result[i++] = new FilesEntry(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4) == 1,
                        resultSet.getLong(5)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public FilesEntry selectOne(int id) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SELECT_QUERY + " WHERE id = " + id);
            if (resultSet.next()) {
                return new FilesEntry(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4) == 1,
                        resultSet.getLong(5)
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
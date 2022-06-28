package org.example.epam.jdbc.service;

import org.example.epam.jdbc.entity.FilesEntry;
import org.example.epam.jdbc.entity.Point;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class PointService implements Closeable {
    private static final String TABLE_NAME = "points";

    private final static String CREATE_QUERY =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                    " (id INT AUTO_INCREMENT PRIMARY KEY," +
                    "x DOUBLE NOT NULL," + "y DOUBLE NOT NULL) ENGINE = INNODB;";
    private final static String DROP_QUERY =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
    private final static String TRUNCATE_QUERY =
            "TRUNCATE " + TABLE_NAME;
    private final static String INSERT_QUERY =
            "INSERT INTO " + TABLE_NAME + " VALUES (null, ?, ?)";
    private final static String UPDATE_QUERY =
            "UPDATE " + TABLE_NAME + " SET x = ?, y = ? WHERE id = ?";
    private final static String DELETE_QUERY =
            "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
    private final static String SELECT_QUERY =
            "SELECT id, x, y FROM " + TABLE_NAME;
    private final static String COUNT_QUERY =
            "SELECT COUNT(*) FROM " + TABLE_NAME;

    private final Connection connection;

    public PointService() {
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

    public Integer insert(Point point) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDouble(1, point.getX());
            statement.setDouble(2, point.getY());
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

    public boolean fillRandom(int maxValue, int totalValues) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
            connection.setAutoCommit(false);
            for (int i = 0; i < totalValues; i++) {
                statement.setDouble(1, (int) (Math.random() * 2 * maxValue - maxValue));
                statement.setDouble(2, (int) (Math.random() * 2 * maxValue - maxValue));
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public int count() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(COUNT_QUERY);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
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

    public Point[] selectAll() {
        Point[] points = new Point[count()];
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SELECT_QUERY);
            int i = 0;
            while (resultSet.next()) {
                points[i++] = new Point(resultSet.getDouble(2), resultSet.getDouble(3));
            }
            return points;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
}



package org.example.epam.jdbc.utils;

import org.example.epam.jdbc.entity.FilesEntry;
import org.example.epam.jdbc.service.FilesService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class FilesUtils {
    private static final String PATH = "D:\\mdocs\\programming\\Projects\\java\\IntelliJ IDEA\\Epam\\JDBC\\src\\main\\resources\\db-entries.txt";

    public static String getFullPath(FilesService filesService, int id) {
        FilesEntry filesEntry = filesService.selectOne(id);
        return (filesEntry.getParentId() == 0)
                ? "\\\\" + filesEntry.getName()
                : getFullPath(filesService, filesEntry.getParentId()) + "\\" + filesEntry.getName();
    }

    public static int getNumberOfFilesIn(FilesService filesService, int id) {
        FilesEntry filesEntry = filesService.selectOne(id);
        if (filesEntry == null || !filesEntry.isDirectory()) {
            return 0;
        }
        FilesEntry[] filesEntries = filesService.selectAll(id);
        if (filesEntries == null || filesEntries.length == 0) {
            return 0;
        }
        int count = 0;
        for (FilesEntry file : filesEntries) {
            count += (file.isDirectory()) ? getNumberOfFilesIn(filesService, file.getId()) : 1;
        }
        return count;
    }

    public static long getDirectorySize(FilesService filesService, int id) {
        long result = 0;
        FilesEntry[] files = filesService.selectAll(id);
        for (FilesEntry filesEntry : files) {
            if (!filesEntry.isDirectory()) {
                result += filesEntry.getDiscUsage();
            }
        }
        return result;
    }

    public static String[] findByMask(String mask) {
        //TODO findByMask
        return null;
    }

    public static boolean moveAll(int sourceId, int destinationId) {
        //TODO moveAll
        return true;
    }

    public static void deleteAllIn(FilesService filesService, int id) {
        ArrayList<FilesEntry> entriesToDelete = new ArrayList<>();
        getAllEntriesIn(filesService, id, entriesToDelete);
        entriesToDelete.forEach(e -> filesService.delete(e.getId()));
    }

    public static void fillTableFromDisc(FilesService filesService, File directory, Integer parentId) throws IOException {
        if (directory.isDirectory()) {
            int id = filesService.insert(directory.getName(), parentId, true, null);
            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            for (File file : files) {
                fillTableFromDisc(filesService, file, id);
            }
        } else {
            filesService.insert(directory.getName(), parentId, false, Files.size(Path.of(directory.getAbsolutePath())));
        }
    }

    public static void fillTableFromResources(FilesService filesService) {
        try {
            List<String> lines = Files.readAllLines(Path.of(PATH));
            for (String line : lines) {
                String[] values = line.split(",");
                filesService.insert(
                        values[1],
                        values[2].equals("NULL") ? null : Integer.parseInt(values[2]),
                        Integer.parseInt(values[3]) == 1,
                        values[4].equals("NULL") ? null : Long.parseLong(values[4]));
            }
        } catch (IOException e) {
           e.printStackTrace();
        }
    }

    public static FilesEntry[] getAllEntries(FilesService filesService) {
        return filesService.selectAll();
    }

    public static FilesEntry[] getAllEntriesIn(FilesService filesService, Integer parentId) {
        ArrayList<FilesEntry> result = new ArrayList<>();
        getAllEntriesIn(filesService, parentId, result);
        return result.toArray(FilesEntry[]::new);
    }

    private static void getAllEntriesIn(FilesService filesService, Integer parentId, ArrayList<FilesEntry> arrayList) {
        FilesEntry[] entries = filesService.selectAll(parentId);
        if (entries != null && entries.length > 0) {
            arrayList.addAll(List.of(entries));
            for (FilesEntry entry : entries) {
                if (entry.isDirectory()) {
                    getAllEntriesIn(filesService, entry.getId(), arrayList);
                }
            }
        }
    }

}

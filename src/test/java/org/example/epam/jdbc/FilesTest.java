package org.example.epam.jdbc;

import org.example.epam.jdbc.entity.FilesEntry;
import org.example.epam.jdbc.service.FilesService;
import org.example.epam.jdbc.utils.FilesUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilesTest {
    private static final String DIR = "d:\\downloads\\Programs\\";
    private static final FilesService filesService = new FilesService();


    @Test
    void testService() {
        assertTrue(filesService.createTable());
        assertTrue(filesService.dropTable());
        assertTrue(filesService.createTable());
        assertEquals(filesService.insert("ABBYY FineReader 11.0.113.164 Corporate Edition Lite RePack by elchupakabra", null, true, null), 1);
        assertEquals(filesService.insert("ABBYY FineReader 11.0.113.164 Corporate Edition Lite RePack by elchupakabra", 1, true, null), 2);
        assertEquals(filesService.insert("ABBYY FineReader 11.0.113.164 Corporate Edition Lite RePack by elchupakabra", 1, false, 50L), 3);
        assertEquals(filesService.count(), 3);
        filesService.delete(1);
        assertEquals(filesService.count(), 2);
        assertTrue(filesService.truncateTable());
        assertEquals(filesService.count(), 0);
    }

    @Test
    void testFillTableFromDisc() {
        try {
            filesService.dropTable();
            filesService.createTable();
            FilesUtils.fillTableFromDisc(filesService, new File(DIR), null);
            assertEquals(filesService.count(), 357);
            FilesEntry[] entries = FilesUtils.getAllEntries(filesService);
            assertEquals(entries.length, 357);
            assertEquals(entries[356].getName(), "uiso9_pe.exe");
            assertEquals(entries[356].getDiscUsage().longValue(), 5104240L);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFillTableFromResources() {
            filesService.dropTable();
            filesService.createTable();
            FilesUtils.fillTableFromResources(filesService);
            assertEquals(filesService.count(), 357);
            FilesEntry[] entries = FilesUtils.getAllEntries(filesService);
            assertEquals(entries.length, 357);
            assertEquals(entries[356].getName(), "uiso9_pe.exe");
            assertEquals(entries[356].getDiscUsage().longValue(), 5104240L);
    }

    @Test
    void testSelectAll() {
            FilesEntry[] entries = FilesUtils.getAllEntriesIn(filesService, 25);
            assertEquals(entries.length, 4);

    }

    @Test
    void testDeleteAllIn() {
            FilesUtils.deleteAllIn(filesService, 25);
            assertEquals(filesService.count(), 353);

    }

    @Test
    void testGetDirectorySize() {
        long directorySize = FilesUtils.getDirectorySize(filesService, 25);
        assertEquals(directorySize, 0L);
    }

    @Test
    void testGetFullPath() {
        assertEquals("\\\\Programs\\Lizardtech DjVu Document Express Editor Pro 6.0.1 build 1320 Portable by punsh\\App\\IRIS_OCR\\ljeng32.dll", FilesUtils.getFullPath(filesService, 123));
    }

    @Test
    void testNumberOfFilesIn() {
        assertEquals(FilesUtils.getNumberOfFilesIn(filesService, 25), 3);
        FilesUtils.deleteAllIn(filesService, 25);
        assertEquals(FilesUtils.getNumberOfFilesIn(filesService, 25), 0);

    }

    @Test
    void testUpdate() {
        assertEquals("CatalogueEntry{id=1, name='Programs', parentId=0, isDirectory=true, discUsage=0}", filesService.update(new FilesEntry(1, "Programs1", null, true, null)).toString());
        assertEquals("CatalogueEntry{id=1, name='Programs1', parentId=0, isDirectory=true, discUsage=0}", filesService.selectOne(1).toString());
    }
}



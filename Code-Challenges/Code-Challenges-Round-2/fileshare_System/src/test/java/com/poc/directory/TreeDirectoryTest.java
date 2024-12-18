package com.poc.directory;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TreeDirectoryTest {

    TreeDirectory treeDirectory = new TreeDirectory();

    @Test
    void listAllFiles() throws IOException {
        treeDirectory.listAllFiles("");
    }

    @Test
    void isFileExists() {
        treeDirectory.isFileExists("");
    }

    @Test
    void createNewFile() {
        treeDirectory.createNewFile("","");
    }

    @Test
    void removeFile() {
        treeDirectory.removeFile("");
    }

    @Test
    void testListAllFiles() throws IOException {
        treeDirectory.listAllFiles("");
    }

    @Test
    void testIsFileExists() {
        treeDirectory.isFileExists("");
    }

    @Test
    void testCreateNewFile() {
        treeDirectory.createNewFile("","");
    }

    @Test
    void testRemoveFile() {
        treeDirectory.removeFile("");
    }

    @Test
    void testListAllFiles1() {
    }

    @Test
    void testIsFileExists1() {
    }

    @Test
    void testCreateNewFile1() {
    }

    @Test
    void testRemoveFile1() {
    }
}
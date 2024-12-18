package com.poc.directory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeDirectory {

    Set<String> listAllFiles(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    boolean isFileExists(String fileName){

        File f = new File(fileName);
        if(f.exists() && !f.isDirectory()) {
            return true;
        }

        return false;
    }

    boolean createNewFile(String fileName, String content){
        try {
            java.io.File arquivo = new java.io.File("c://", fileName);
            boolean statusArq = arquivo.createNewFile();
            System.out.print(statusArq);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }

    boolean removeFile(String filename){
        return false;
    }


}

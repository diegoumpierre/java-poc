package com.poc.rendertemplate.service;

import com.opencsv.CSVWriter;
import com.poc.rendertemplate.dto.User;

import java.io.FileWriter;
import java.util.List;

public class PrintCSV implements PrintApplication {

    @Override
    public void printFile(List<User> userList) {
        try {
            userList.stream().forEach(user -> {
                try {
                    CSVWriter writer = new CSVWriter(new FileWriter(user.getFileName() + ".csv"));
                    writer.writeNext(new String[]{"Name", "Email", "Age"});
                    writer.writeNext(new String[]{user.getName(), user.getEmail(), String.valueOf(user.getAge())});
                    writer.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            System.out.println("Error to generate de CSV ERROR->" + e.getMessage());
        }
    }
}

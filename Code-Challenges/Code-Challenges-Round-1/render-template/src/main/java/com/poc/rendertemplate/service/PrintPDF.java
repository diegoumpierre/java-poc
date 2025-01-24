package com.poc.rendertemplate.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.poc.rendertemplate.dto.User;

import java.io.FileOutputStream;
import java.util.List;

public class PrintPDF implements PrintApplication {


    @Override
    public void printFile(List<User> userList) {
        try {
            userList.stream().forEach(user -> {
                Document document = new Document();
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(user.getFileName() + ".pdf"));
                    document.open();
                    document.add(new Paragraph("Name: " + user.getName()));
                    document.add(new Paragraph("E-mail: " + user.getEmail()));
                    document.add(new Paragraph("Age: " + user.getAge()));
                    document.close();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            System.out.println("Error to generate de PDF ERROR->" + e.getMessage());
        }
    }
}

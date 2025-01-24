package com.poc.rendertemplate.service;

import com.poc.rendertemplate.dto.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.FileWriter;
import java.util.List;

public class PrintHTML implements PrintApplication{
    @Override
    public void printFile(List<User> userList) {

        try {
            userList.stream().forEach(user -> {
                try {
                    TemplateEngine templateEngine = new TemplateEngine();
                    Context context = new Context();
                    context.setVariable("name", user.getName());
                    context.setVariable("email", user.getEmail());
                    context.setVariable("age", user.getAge());
                    String htmlContent = templateEngine.process(getTemplate(), context);
                    FileWriter writer = new FileWriter(user.getFileName()+".html");
                    writer.write(htmlContent);
                    writer.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            System.out.println("Error to generate de HTML ERROR->" + e.getMessage());
        }
    }

    private String getTemplate() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns:th=\"http://www.w3.org/1999/xhtml\">\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <title>User - \"${name}\"</title>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Name: <span th:text=\"${name}\"></span></h1>\n" +
                "        <p>E-mail: <span th:text=\"${email}\"></span></p>\n" +
                "        <p>Age: <span th:text=\"${age}\"></span></p>\n" +
                "    </body>\n" +
                "</html>";
    }
}

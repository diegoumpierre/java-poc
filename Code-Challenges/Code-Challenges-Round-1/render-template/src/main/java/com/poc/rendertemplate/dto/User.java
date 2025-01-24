package com.poc.rendertemplate.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {

    private String name;
    private String email;
    private int age;

    public String getFileName() {
        String filename = name.toLowerCase() +"_"+ email.toLowerCase();
        filename = filename.replaceAll(" ", "_");
        filename = filename.replaceAll("@", "");
        filename = filename.replaceAll("_", "");
        return filename;
    }
}

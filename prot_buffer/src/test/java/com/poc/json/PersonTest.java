package com.poc.json;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PersonTest {

    private String path = System.getProperty("user.dir")+"/example_json.txt";

    @Test
    void serialize() throws IOException {

        Person person = new Person();
        person.setName("Diego Umpierre");
        person.setId(101001);
        person.setEmail("diego@umpierre.com.br");

        JSONObject jsonObject = new JSONObject(person);
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(jsonObject.toString().getBytes());

    }


    @Test
    void deserialize() throws IOException {

        String expectedName = "Diego Umpierre";
        String expectedEmail = "diego@umpierre.com.br";
        int expectedId = 101001;


        File file = new File(path);
        String dados = new String(Files.readAllBytes(file.toPath()));
        JSONObject jsonObject = new JSONObject(dados);

        Person person = new Person();
        person.setEmail(jsonObject.getString("email"));
        person.setName(jsonObject.getString("name"));
        person.setId(jsonObject.getInt("id"));

        assertEquals(expectedEmail, person.getEmail());
        assertEquals(expectedId, person.getId());
        assertEquals(expectedName, person.getName());


    }


}
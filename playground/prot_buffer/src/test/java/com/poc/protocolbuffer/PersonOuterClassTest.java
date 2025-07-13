package com.poc.protocolbuffer;

import org.junit.jupiter.api.Test;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PersonOuterClassTest {


    private String path = System.getProperty("user.dir")+"/example_prot_buff2.txt";
    private String otherPath = System.getProperty("user.dir")+"/example_prot_buff2.txt";


    @Test
    void createNewInstance(){

        int expectedId = new Random().nextInt();
        String expectedName = "Diego Umpierre";
        String expectedEmail = "diego@umpierre.com.br";

        PersonOuterClass.Person person =
                PersonOuterClass.Person.newBuilder()
                        .setId(expectedId)
                        .setName(expectedName)
                        .setEmail(expectedEmail)
                        .build();


        assertEquals(expectedEmail, person.getEmail());
        assertEquals(expectedId, person.getId());
        assertEquals(expectedName, person.getName());
    }


    @Test
    void serializedPerson() throws IOException {

        String expectedName = "Diego Umpierre";
        String expectedEmail = "diego@umpierre.com.br";
        int expectedId = 101010;

        PersonOuterClass.Person person =
                PersonOuterClass.Person.newBuilder()
                        .setId(expectedId)
                        .setName(expectedName)
                        .setEmail(expectedEmail)
                        .build();
        FileOutputStream fos = new FileOutputStream(path);
        person.writeTo(fos);
    }

    @Test
    void deserializedPerson() throws IOException {

        String expectedName = "Diego Umpierre";
        String expectedEmail = "diego@umpierre.com.br";
        int expectedId = 101010;

        PersonOuterClass.Person person
                = PersonOuterClass.Person.newBuilder()
                .mergeFrom(new FileInputStream(path)).build();

        assertEquals(expectedEmail, person.getEmail());
        assertEquals(expectedId, person.getId());
        assertEquals(expectedName, person.getName());
    }

    @Test
    void deserializedPersonOld() throws IOException {

        String expectedName = "Diego Umpierre";
        String expectedEmail = "diego@umpierre.com.br";
        int expectedId = 101010;

        PersonOuterClass.Person person
                = PersonOuterClass.Person.newBuilder()
                .mergeFrom(new FileInputStream(otherPath)).build();

        assertEquals(expectedEmail, person.getEmail());
        assertEquals(expectedId, person.getId());
        assertEquals(expectedName, person.getName());
    }

}
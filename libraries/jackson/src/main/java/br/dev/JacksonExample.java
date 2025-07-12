package br.dev;

import com.fasterxml.jackson.databind.ObjectMapper;


public class JacksonExample {

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Person person = new Person("Diego", 40);

        // Convert Java object to JSON string
        String json = mapper.writeValueAsString(person);
        System.out.println("JSON: " + json);

        String jsonInput = "{\"name\":\"UMPIERRE\",\"age\":50}";

        Person person2 = mapper.readValue(jsonInput, Person.class);
        System.out.println("Deserialized: " + person2.name + ", " + person2.age);
    }



}

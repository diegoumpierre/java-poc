package br.dev;

import com.google.gson.Gson;

public class GsonExample {

    public static void main(String[] args) {
        Gson gson = new Gson();

        Person person = new Person("Diego", 35);
        String json = gson.toJson(person);
        System.out.println("Serialized JSON: " + json);

        String jsonString = "{\"name\":\"Diego\",\"age\":40}";
        Person person2 = gson.fromJson(jsonString, Person.class);
        System.out.println("Deserialized Object: " + person2.getName() + ", " + person2.getAge());
    }


}

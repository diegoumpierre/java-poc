package br.dev;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsingHashMap {

    /**
     * You can use a HashMap to store key-value pairs in Java.
     * To print the final JSON like you want
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {

        Person person = new Person("Diego", 40);
        Person person2 = new Person("UMPIERRE", 50);
        Person person3 = new Person("JOHN", 30);
        Person person4 = new Person("JANE", 25);

        List<Object> objectList = new ArrayList<>();
        objectList.add(person);
        objectList.add(person2);

        List<Object> groupList = new ArrayList<>();
        groupList.add(person3);
        groupList.add(person4);

        // Convert Java object to JSON string
        HashMap<String, Object> customJson = new HashMap<>();
        customJson.put("GroupPrincipal", objectList);
        customJson.put("Clients", groupList);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(customJson);

        System.out.println(json);

        /**
         * {
         *   "GroupPrincipal": [
         *     {
         *       "name": "Diego",
         *       "age": 40
         *     },
         *     {
         *       "name": "UMPIERRE",
         *       "age": 50
         *     }
         *   ],
         *   "Clients": [
         *     {
         *       "name": "JOHN",
         *       "age": 30
         *     },
         *     {
         *       "name": "JANE",
         *       "age": 25
         *     }
         *   ]
         * }
         */

    }

}

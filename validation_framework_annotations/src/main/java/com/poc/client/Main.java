package com.poc.client;

import com.poc.client.client1.domain.Person;
import com.poc.client.client1.service.PersonServiceHelper;

public class Main {


    public static void main(String[] args) throws Exception {

        //client1
        Person person = new Person("Diego", 30, "81401310087");
        PersonServiceHelper personServiceHelper = new PersonServiceHelper();
        personServiceHelper.doSomeProcess(person);

        //client2



    }



}

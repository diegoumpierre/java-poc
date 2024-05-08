package com.poc.library.service;

import com.poc.client.client1.domain.Person;
import org.junit.jupiter.api.Test;

class ProcessServiceProxyTest {

    @Test
    void validate() throws IllegalAccessException {
        Person person = new Person("na",null, null);

        ProcessServiceProxy processServiceProxy = new ProcessServiceProxy();
//        processServiceProxy.validate(person);

    }
}
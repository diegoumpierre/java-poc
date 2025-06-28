package br.com.poc.service;

import br.com.poc.entity.Person;

public class PersonService<T> {

    public void save(Person person) {
        System.out.println("Save the person");
    }
}

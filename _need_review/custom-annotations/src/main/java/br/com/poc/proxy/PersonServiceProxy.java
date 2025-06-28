package br.com.poc.proxy;

import br.com.poc.entity.Person;
import br.com.poc.service.PersonService;
import br.com.poc.validator.PersonValidator;

import java.util.List;

public class PersonServiceProxy<T> extends PersonService<T> {
    public void save(Person person) {

        List<String> errors = null;

        // validation here
        PersonValidator personValidator = new PersonValidator();
        try {
            errors = personValidator.validate(person);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (errors != null && errors.size() > 0){
            for(String error :errors){
                System.out.println(error);
            }
        }else {
            super.save(person);
        }


        // cache reflaction

    }
}

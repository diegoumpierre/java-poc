package br.com.poc;

import br.com.poc.entity.Person;
import br.com.poc.factory.PersonServiceFactory;
import br.com.poc.service.PersonService;

public class App {

    public static void main(String[] args) {


        PersonService service = PersonServiceFactory.newInstance();
        service.save(
                Person.builder()
                        .name("Diego")
                        .birthDate("fffff")
                        .build());
    }
}

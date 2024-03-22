package com.poc.domain.validation;

import com.poc.domain.Person;
import org.junit.jupiter.api.Test;

class AnnotationValidationTest {


    AnnotationValidation annotationValidation = new AnnotationValidation();

    @Test
    void validatePessoaNotNullAgeShouldFail() throws Exception {

        Person person = new Person();
        person.setName("JOA");

        annotationValidation.validate(person);

        //expects a exception

    }
}
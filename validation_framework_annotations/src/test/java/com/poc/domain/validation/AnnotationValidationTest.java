package com.poc.domain.validation;

import com.poc.domain.Person;
import com.poc.validation.AnnotationValidation;
import org.junit.Assert;
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

    @Test
    void isValidCPFShouldSucces() {
        String cpf = "81401310087";
        Assert.assertTrue(annotationValidation.isValidCPF(cpf));
    }

    @Test
    void isValidCPFShouldFail() {
        String cpf = "81401320087";
        Assert.assertFalse(annotationValidation.isValidCPF(cpf));
    }

    @Test
    void isNotNullShouldFail() {
        String name = "";
        Assert.assertFalse(annotationValidation.isValidName(name));
    }

    @Test
    void isNotNullShouldSuccess() {
        String name = "Dieg";
        Assert.assertTrue(annotationValidation.isValidName(name));
    }



}
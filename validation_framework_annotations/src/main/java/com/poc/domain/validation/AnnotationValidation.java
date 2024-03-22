package com.poc.domain.validation;

import com.poc.domain.Person;
import com.poc.domain.annotation.NotNull;

import java.lang.reflect.Field;

public class AnnotationValidation {
    public void validate(Person person) {

//        String className = person.getClass().getName();
        Field[] fields = person.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                //check if the field have the alias
                String notNull= String.valueOf(field.getAnnotation(NotNull.class));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean isValidateNotNull(String value){
        if (value != null && !value.isEmpty()){
            return true;
        }
        return false;
    }
}
package com.poc.library.validation;

public class NameValidation  implements AnnotationValidationInterface{

    @Override
    public boolean isValid(String value){
        if (value == null || value.isEmpty() || value.length() < 3 ) return false;
        return true;
    }

}

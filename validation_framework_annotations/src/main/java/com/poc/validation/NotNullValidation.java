package com.poc.validation;

public class NotNullValidation  implements AnnotationValidationInterface{
    @Override
    public boolean isValid(Object value) {
        return false;
    }
}

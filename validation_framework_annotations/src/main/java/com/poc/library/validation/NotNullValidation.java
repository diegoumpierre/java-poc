package com.poc.library.validation;

public class NotNullValidation  implements AnnotationValidationInterface{
    @Override
    public boolean isValid(String value){
        if (value != null && !value.isEmpty()){
            return true;
        }
        return false;
    }
}

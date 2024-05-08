package com.poc.library.validation;

import com.poc.library.annotation.Cpf;
import com.poc.library.annotation.Name;
import com.poc.library.annotation.NotNull;

import java.lang.reflect.Field;

public class AnnotationValidation {

    private CpfValidation cpfValidation = new CpfValidation();
    private NameValidation nameValidation = new NameValidation();
    private NotNullValidation notNullValidation = new NotNullValidation();

    public void validate(Object objectToValidate) throws Exception {
        try {
            Field[] fields = objectToValidate.getClass().getDeclaredFields();

            for (Field field : fields) {

                field.setAccessible(true);
                Object fieldValue = field.get(objectToValidate);

                if (field.getAnnotation(NotNull.class) != null) {
                    if (!notNullValidation.isValid((String) fieldValue)){
                        throw new Exception("The field "+field.getName()+" can't be null!");
                    }
                }

                if (field.getAnnotation(Name.class) != null) {
                    if (!nameValidation.isValid((String) fieldValue)){
                        throw new Exception("The field "+field.getName()+" need have 3 letters in the minimum!");
                    }
                }

                if (field.getAnnotation(Cpf.class) != null) {
                    if (!cpfValidation.isValid((String) fieldValue)){
                        throw new Exception("The field "+field.getName()+" have a invalid CPF!");
                    }
                }
            }
        }catch (IllegalAccessException illegalAccessException){
            throw new RuntimeException("Wrong access in the field! ");
        }
    }
}
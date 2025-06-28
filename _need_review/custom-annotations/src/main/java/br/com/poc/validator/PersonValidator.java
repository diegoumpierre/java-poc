package br.com.poc.validator;

import br.com.poc.annotation.DateField;
import br.com.poc.annotation.NumberField;
import br.com.poc.annotation.RequiredField;
import br.com.poc.annotation.StringField;
import br.com.poc.entity.Person;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PersonValidator {

    private final String MSG_ERROR = "FIELD: {0}, ERROR: {1}";
    private final String MSG_ERROR_VALUE_DATE = "The value: {0} it is not a valid value to format: {1}";

    private List<String> result = new ArrayList<>();

    public List<String> validate(Person person) throws IllegalAccessException {
        for(Field field : person.getClass().getDeclaredFields()){

            field.setAccessible(true);
            Object value = field.get(person);

            if (field.getAnnotation(DateField.class) != null) addOnListError(validateDateField(field,value));
            if (field.getAnnotation(NumberField.class) != null) addOnListError(validateNumberField(field));
            if (field.getAnnotation(RequiredField.class) != null) addOnListError(validateRequiredField(value,field.getName()));
            if (field.getAnnotation(StringField.class) != null) addOnListError(validateStringField(field));

        }
        return result;

    }
    private void addOnListError(String errorValidate){
        if (errorValidate != null)  result.add(errorValidate);
    }

    private String validateDateField(Field field,Object value) throws IllegalAccessException {
        String validateTypeString = validateStringField(field);

        if (validateTypeString != null) return validateTypeString;
        if (validateRequiredField(value,field.getName()) == null){
            String pattern = field.getAnnotation(DateField.class).format();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            try{
                Date date = simpleDateFormat.parse(value.toString());
            }catch (ParseException parseException){
                return MessageFormat.format(
                    MSG_ERROR
                    ,field.getName()
                    , MessageFormat.format(MSG_ERROR_VALUE_DATE,value,pattern));

            }

        }

        return null;
    }

    private String validateNumberField(Field field){
        if (!Integer.class.getName().equals(field.getType().getName()))
            return MessageFormat.format(
                    MSG_ERROR
                    ,field.getName()
                    ,"The filed need be "+Integer.class.getName()+"!"
            );

        return null;
    }

    private String validateRequiredField(Object value,String fieldName) throws IllegalAccessException {
        if (value == null || value.toString().trim() =="")
            return MessageFormat.format(
                    MSG_ERROR
                    ,fieldName
                    ,"The is required, can not be null or blank!"
            );

        return null;
    }

    private String validateStringField(Field field){
        if (!String.class.getName().equals(field.getType().getName()))
            return MessageFormat.format(
                    MSG_ERROR
                    ,field.getName()
                    ,"The filed need be "+String.class.getName()+"!"
                    );

        return null;
    }



}

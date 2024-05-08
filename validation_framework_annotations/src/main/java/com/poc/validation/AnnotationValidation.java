package com.poc.validation;

import com.poc.domain.Person;
import com.poc.annotation.NotNull;

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

    protected boolean isValidateNotNull(String value){
        if (value != null && !value.isEmpty()){
            return true;
        }
        return false;
    }


    protected boolean isValidName(String value){
        if (value.isEmpty()) return false;
        return true;
    }

    private static final int[] WeightCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

    public boolean isValidCPF(String cpf) {
        cpf = cpf.trim().replace(".", "").replace("-", "");
        if (cpf.length() != 11) return false;

        for (int j = 0; j < 10; j++)
            if (padLeft(Integer.toString(j), Character.forDigit(j, 10)).equals(cpf))
                return false;

        int Digit01 = calculateDigit(cpf.substring(0,9));
        int Digit02 = calculateDigit(cpf.substring(0,9) + Digit01);
        return cpf.equals(cpf.substring(0,9) + Digit01 + Digit02);
    }

    private static int calculateDigit(String str) {
        int sum = 0;
        for (int index =str.length()-1, digit; index >= 0; index-- ) {
            digit = Integer.parseInt(str.substring(index,index+1));
            sum += digit* WeightCPF[ WeightCPF.length-str.length()+index];
        }
        sum = 11 - sum % 11;
        return sum > 9 ? 0 : sum;
    }

    private static String padLeft(String text, char character) {
        return String.format("%11s", text).replace(' ', character);
    }
}
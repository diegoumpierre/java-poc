package com.umpierre.annotation.anottation;

import com.umpierre.annotation.anottation.model.Customer;
import com.umpierre.annotation.anottation.personal.ShowField;
import com.umpierre.annotation.anottation.personal.ShowMethod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainTest {


    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {
        System.out.println("Java Custom Annotation Example");
        System.out.println();

        Customer customer = Customer.builder().id(1L).name("Diego Umpierre").age(39).build();

        for (Field field : customer.getClass().getDeclaredFields()) {
            ShowField dbField = field.getAnnotation(ShowField.class);
            System.out.println("field name: " + dbField.name());

            // changed the access to public
            field.setAccessible(true);
            Object value = field.get(customer);
            System.out.println("field value: " + value);
            System.out.println("field type: " + dbField.type());
            System.out.println();
        }


        for (Method method : customer.getClass().getDeclaredMethods()) {

            if (method.isAnnotationPresent(ShowMethod.class)) {
                method.invoke(customer);
            }
        }
    }

}

//ServicePerson service = ServicePerson()
//
//service.save(new Person())
//
//        ServicePerson {
//        save(person)
//        {
//            personArray.add(person)
//        }        }
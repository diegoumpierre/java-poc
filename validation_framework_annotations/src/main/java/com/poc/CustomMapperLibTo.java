package com.poc;

import br.com.poc.service.mapper.converter.template.ClassStructure;
import org.springframework.core.convert.converter.Converter;

public class CustomMapperLibTo extends CustomMapper{

    private Object origin;

    //create a method to change the origin

    public CustomMapperLibTo(Object origin) {
        super();
        this.origin = origin;
    }

    public <T> T to(Class<T> destinyClass) {
        try {
            T target = destinyClass.getDeclaredConstructor().newInstance();

            ClassStructure classStructure = new ClassStructure();
            String classNameConverter = classStructure.generateClassConverterName(origin.getClass().getName(), target.getClass().getName());

            //here I have the existent converter list
            if (!converterClassCreatedMap.containsKey(classNameConverter)){
                readClass(origin);
                readClass(target);
                generateTheClassAndGetInstance(classStructure, classNameConverter, origin.getClass().getName(), target.getClass().getName());
            }
            return (T) ((Converter) converterClassCreatedMap.get(classNameConverter)).convert(origin);
        }catch (Exception error){
            System.out.println("Error try to convert. ERROR= "+error.getMessage());
        }
        return null;
    }
}
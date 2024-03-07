package com.poc;

public class CustomMapperLib extends CustomMapper {

    //create a local CustomMapperLibTo and just use the method on line 8

    public CustomMapperLibTo from(Object origin) {
        return new CustomMapperLibTo(origin);
    }
}
package com.poc.client.client2.domain;

import com.poc.library.annotation.Name;
import com.poc.library.annotation.NotNull;

public class Car {

    public Car(){
    }
    public Car(String name, Integer age){
        this.name = name;
        this.age = age;
    }

    @NotNull
    @Name
    private String name;

    @NotNull
    private Integer age;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

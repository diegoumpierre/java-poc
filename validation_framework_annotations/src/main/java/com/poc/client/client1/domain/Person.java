package com.poc.client.client1.domain;

import com.poc.library.annotation.Cpf;
import com.poc.library.annotation.Name;
import com.poc.library.annotation.NotNull;

public class Person {

    public Person(){

    }
    public Person(String name,Integer age,String cpf){
        this.name = name;
        this.age = age;
        this.cpf = cpf;
    }

    @Name
    private String name;

    @Cpf
    private String cpf;


    @NotNull
    private Integer age;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

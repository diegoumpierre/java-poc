package com.poc.domain;

import com.poc.domain.annotation.Cpf;
import com.poc.domain.annotation.Name;
import com.poc.domain.annotation.NotNull;

public class Person {

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

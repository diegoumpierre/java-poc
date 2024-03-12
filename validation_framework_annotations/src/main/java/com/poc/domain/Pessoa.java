package com.poc.domain;

import com.poc.domain.annotation.Cpf;
import com.poc.domain.annotation.Name;
import com.poc.domain.annotation.NotNull;

public class Pessoa {

    @Name
    private String name;

    @Cpf
    private String cpf;


    @NotNull
    private Integer age;

}

package br.com.poc.entity;

import br.com.poc.annotation.DateField;
import br.com.poc.annotation.NumberField;
import br.com.poc.annotation.RequiredField;
import br.com.poc.annotation.StringField;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Person {

    @RequiredField
    @StringField
    private String name;

    @NumberField
    private Integer age;

    @DateField(format="yyyy-MM-dd")
    private String birthDate;

}

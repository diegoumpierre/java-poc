package com.umpierre.annotation.anottation.model;

import com.umpierre.annotation.anottation.personal.ShowField;
import com.umpierre.annotation.anottation.personal.ShowMethod;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer {


    @ShowField(name="id", type = Long.class)
    private Long id;

    @ShowField(name="nameStr", type = String.class)
    private String name;

    @ShowField(name = "theAge", type = Integer.class)
    private int age;


    @ShowMethod
    public void print(){
        System.out.println("print: "+name+" - "+age);
    }


    @ShowMethod
    public void print2(){
        System.out.println("print2: "+id);
    }

}

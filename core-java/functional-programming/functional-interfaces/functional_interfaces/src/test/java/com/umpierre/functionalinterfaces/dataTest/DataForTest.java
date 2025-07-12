package com.umpierre.functionalinterfaces.dataTest;

import com.umpierre.functionalinterfaces.domain.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataForTest {

    public static List<Person> gimmeListPerson(){
        List<Person> personList = new ArrayList<>();

        personList.add(Person.builder().name("JOHN").companyId("30").country("SPAIN").build());
        personList.add(Person.builder().name("MARY").companyId("30").country("BRAZIL").build());
        personList.add(Person.builder().name("ALEX").companyId("45").country("ITALY").build());
        personList.add(Person.builder().name("ROBERT").companyId("32").country("PARIS").build());
        personList.add(Person.builder().name("DE NIRO").companyId("30").country("BRAZIL").build());
        personList.add(Person.builder().name("BRAD").companyId("32").country("PARIS").build());

        return personList;
    }

    public static List<Integer> gimmeListOneToTen(){
        return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    public static List<String> gimmeListNames(){
        return Arrays.asList("Diego","Daniel","Tiogo","Marcelo","Marco","Antonio","Mark","Dani");
    }

}

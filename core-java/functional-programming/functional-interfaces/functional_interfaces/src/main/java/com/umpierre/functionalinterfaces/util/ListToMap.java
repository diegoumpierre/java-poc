package com.umpierre.functionalinterfaces.util;

import com.umpierre.functionalinterfaces.domain.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ListToMap{

    public Map<String,List<Person>> convertListToMap(List<Person> personList, Function<Person, String> functionParameter) {

        Map<String, List<Person>> mapResult = new HashMap<>();

        List<Person> lst = null;

        for(Person person :personList){
            lst = mapResult.get(functionParameter.apply(person));

            if (lst == null){
                lst = new ArrayList<>();
            }
            lst.add(person);
            mapResult.put(functionParameter.apply(person),lst);
        }
        return mapResult;

    }



    public String getCompanyId(Person p) {
        return p.getCompanyId();
    }

    public String getCountry(Person p) {
        return p.getCountry();
    }

}

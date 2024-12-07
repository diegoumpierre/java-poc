package com.example.problems.dpk03_lookup;

import java.util.Map;

public class DPK03_impl_1 {

   public static String lookup(int i) {
        Map<Integer, String> map = Map.of(1, "John");
        if (map.containsKey(i)) return map.get(i);
        return null;
    }


    public static String lookup(String nameOrEmail) {
        Map<String, String> map = Map.of("John","john@john.jhon.com");
        if (map.containsKey(nameOrEmail)){
            return map.get(nameOrEmail);
        }else{
            for(String name :map.keySet()){
                if (nameOrEmail.equals(map.get(name))){
                    return name;
                }
            }
        }
        return null;
    }
}

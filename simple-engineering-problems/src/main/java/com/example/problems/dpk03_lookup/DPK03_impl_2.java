package com.example.problems.dpk03_lookup;

import java.util.Map;

public class DPK03_impl_2 {
    public static String lookup(int key){
        Map<Integer,String> map = Map.of(1, "John");
        return map.getOrDefault(key,null);
    }
    public static String lookup(String nameEmail){
        Map<String, String> map = Map.of("John","john@john.john.com");
        if(map.containsKey(nameEmail)){
            return map.get(nameEmail);
        }else{
            for(String name : map.keySet()){
                if(map.get(name).equals(nameEmail)){
                    return name;
                }
            }
        }
        return null;
    }
}

package com.example.problems.dpk03_lookup;

import java.util.Map;

public class DPK03_impl_5 {
    public static String lookup(int key){
        Map<Integer,String> map = Map.of(1,"John");
        return map.getOrDefault(key,null);
    }

    public static String lookup(String key) {
        Map<String, String> map = Map.of("John", "john@john.john.com");
        if (map.containsKey(key)){
            return map.get(key);
        }else {
            for(String name : map.keySet()){
                if(key.equals(map.get(name))){
                    return name;
                }
            }
        }
        return null;
    }
}

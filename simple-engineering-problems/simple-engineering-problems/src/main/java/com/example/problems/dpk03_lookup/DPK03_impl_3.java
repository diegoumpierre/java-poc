package com.example.problems.dpk03_lookup;

import java.util.Map;

public class DPK03_impl_3 {
    public static String lookup(int i) {
        Map<Integer,String> map = Map.of(1,"John");
        return map.getOrDefault(i,null);
    }

    public static String lookup(String key) {
        Map<String, String> map = Map.of("John", "john@john.john.com");
        if(map.containsKey(key)){
            return map.get(key);
        }else {
            for(String mapKey : map.keySet()){
                if (map.get(mapKey).equals(key)){
                    return mapKey;
                }
            }
        }
        return null;
    }
}

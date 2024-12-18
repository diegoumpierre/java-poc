package com.example.problems.dpk03_lookup;

import java.util.Map;

public class DPK03_impl_4 {



    public static String lookup(int key){
        Map<Integer,String> map = Map.of(1,"John");
        return map.getOrDefault(key,null);
    }

    public static String lookup(String keyOrValue){
        Map<String,String> map = Map.of("John","john@john.john.com");
        if(map.containsKey(keyOrValue)){
            return map.get(keyOrValue);
        }else{
            for(String name : map.keySet()){
                if (map.get(name).equals(keyOrValue)) return name;
            }
        }
        return null;
    }



}

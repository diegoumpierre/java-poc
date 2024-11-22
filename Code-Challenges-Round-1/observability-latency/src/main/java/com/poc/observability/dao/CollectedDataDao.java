package com.poc.observability.dao;

import com.poc.observability.model.CollectedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectedDataDao {

    private static List<CollectedData> collectData = new ArrayList<>();

    private static Map<String, String> methodMap = new HashMap<>();


    public void configure(String identifier, String method){
        methodMap.put(identifier,method);
    }

    public String getMethodByIdentifier(String identifier){
        return methodMap.getOrDefault(identifier, "NO_IDENTIFIER");
    }

    public CollectedData save(CollectedData collectedData){
        collectData.add(collectedData);
        return collectedData;
    }

    public CollectedData findByIdentifier(String identifier){
        return collectData.stream()
                .filter(data-> data.getIdentifier().equals(identifier))
                .findAny().get();
    }

    public List<CollectedData> findAll() {
        return collectData;
    }
}

package com.poc.observability;

import java.util.ArrayList;

public class WatchImpl implements WatchInterface{

    private String className;

    public WatchImpl(Class classToWatch){
        this.className = classToWatch.getName();
    }


    @Override
    public void start(String methodName) {
        DataCollected dataCollected = new DataCollected();
        dataCollected.setStartTime(System.nanoTime());
        dataCollected.setMethodName(methodName);
        CollectData.dataCollectedMap.getOrDefault(this.className,new ArrayList<>()).add(dataCollected);
    }

    @Override
    public void error() {

    }

    @Override
    public void warning() {

    }

    @Override
    public void stop(String methodName) {

        CollectData.dataCollectedMap.getOrDefault(this.className,new ArrayList<>())
                .stream()
                .forEach(dataCollected -> {
                    if(dataCollected.getMethodName().equals(methodName)){
                        dataCollected.setEndTime(System.nanoTime());
                    }
                });
                


    }
}

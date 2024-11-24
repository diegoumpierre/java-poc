package com.poc.observability.service;

import com.poc.observability.dao.CollectedDataDao;
import com.poc.observability.model.CollectedData;

public class CollectedServiceImpl implements CollectedService {

    private CollectedDataDao collectedDataDao;

    public CollectedServiceImpl() {
        this.collectedDataDao = new CollectedDataDao();
    }

    public void configure(String identifier, String method) {
        collectedDataDao.configure(identifier, method);
    }

    public String getMethodByIdentifier(String identifier) {
        return collectedDataDao.getMethodByIdentifier(identifier);
    }


    public void start(String identifier) {
        CollectedData collectedData = new CollectedData();
        collectedData.setMethodName(getMethodByIdentifier(identifier));
        collectedData.setIdentifier(identifier);
        collectedData.setStartTime(System.nanoTime());
        collectedDataDao.save(collectedData);
    }

    public void end(String identifier, boolean isSuccess) {
        CollectedData collectedData = collectedDataDao.findByIdentifier(identifier);
        collectedData.setEndTime(System.nanoTime());
        if(isSuccess){
            collectedData.setSuccess(true);
        }else{
            collectedData.setError(true);
        }
        collectedDataDao.save(collectedData);
    }

    @Override
    public void printObservability() {

        //how many calls the system have
        //how many errors the system have
        //look the readme
        System.out.println("size--> "+collectedDataDao.findAll().size());

        collectedDataDao.findAll().forEach(collectedData -> {
            System.out.print(collectedData.getIdentifier().toString());
            System.out.println("--"+collectedData.getMethodName().toString());
        });

    }




}

package com.poc.observability.service;

import com.poc.observability.dao.CollectedDataDao;
import com.poc.observability.model.CollectedData;

public class CollectedServiceImpl implements CollectedService {

    private CollectedDataDao collectedDataDao;

    public CollectedServiceImpl(CollectedDataDao collectedDataDao) {
        this.collectedDataDao = collectedDataDao;
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
}

package com.poc.client.client2.service;

import com.poc.library.service.ProcessInterface;
import com.poc.library.service.ProcessServiceProxy;

public class CarServiceHelper implements ProcessInterface {

    ProcessInterface processInterface;

    public CarServiceHelper(){
        this.processInterface = new ProcessServiceProxy();
    }


    @Override
    public boolean doSomeProcess(Object objectToBeProcessed) throws Exception {
        return processInterface.doSomeProcess(objectToBeProcessed);
    }

    @Override
    public int howManyProcess(Object objectToBeProcessed) throws Exception {
        return processInterface.howManyProcess(objectToBeProcessed);
    }
}

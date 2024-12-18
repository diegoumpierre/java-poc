package com.poc.client.client1.service;

import com.poc.library.service.ProcessInterface;
import com.poc.library.service.ProcessServiceProxy;

public class PersonServiceHelper implements ProcessInterface {

    ProcessInterface processInterface;

    public PersonServiceHelper(){
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

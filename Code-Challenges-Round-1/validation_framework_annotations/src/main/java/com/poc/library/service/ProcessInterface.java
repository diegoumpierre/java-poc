package com.poc.library.service;

public interface ProcessInterface {

    boolean doSomeProcess(Object objectToBeProcessed) throws Exception;

    int howManyProcess(Object objectToBeProcessed) throws Exception;
}

package com.poc.library.service;

/**
 * This is the real class call after pass the validation!
 * The validation call is transparent for the user
 */
public class ProcessService implements ProcessInterface{
    @Override
    public boolean doSomeProcess(Object objectToBeProcessed) {
        //TODO add any logic inside the method
        return false;
    }

    @Override
    public int howManyProcess(Object objectToBeProcessed) {
        //TODO add any logic here
        return 10;
    }
}

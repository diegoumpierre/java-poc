package com.poc.library.service;

import com.poc.library.validation.AnnotationValidation;

public class ProcessServiceProxy implements ProcessInterface {

    private ProcessService processService = new ProcessService();
    private AnnotationValidation annotationValidation = new AnnotationValidation();

    @Override
    public boolean doSomeProcess(Object objectToBeProcessed) throws Exception {
        annotationValidation.validate(objectToBeProcessed);
        return processService.doSomeProcess(objectToBeProcessed);
    }

    @Override
    public int howManyProcess(Object objectToBeProcessed) throws Exception {
        annotationValidation.validate(objectToBeProcessed);
        return processService.howManyProcess(objectToBeProcessed);
    }
}
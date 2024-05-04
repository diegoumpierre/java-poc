package com.poc.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpensiveObjectImpl implements ExpensiveObject {

    //private final log = org.apache.log4j.Logger.getLogger(ExpensiveObjectImpl.class);
    public static final Logger LOGGER = LoggerFactory.getLogger(ExpensiveObjectImpl.class);


    @Override
    public void process() {
        LOGGER.info("processing 1 complete.");
    }

    private void heavyInitialConfiguration() {
        LOGGER.info("Loading initial configuration...");
    }


}

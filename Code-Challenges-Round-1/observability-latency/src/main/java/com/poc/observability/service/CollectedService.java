package com.poc.observability.service;

public interface CollectedService {

    void configure(String identifier, String method);

    String getMethodByIdentifier(String identifier);

    void start(String identifier);

    void end(String identifier, boolean isSuccess);

    void printObservability();
}

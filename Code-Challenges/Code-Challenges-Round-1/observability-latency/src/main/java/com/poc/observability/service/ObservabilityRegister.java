package com.poc.observability.service;

import com.poc.observability.dto.MetricDto;
import com.poc.observability.dto.MetricQueueDto;

public class ObservabilityRegister {

    public static void start(String method, String path){
        String key = method+path;
        MetricDto metricDto = MetricQueueDto.metricMap.getOrDefault(key, new MetricDto());
        metricDto.setMethod(method);
        metricDto.setPath(path);
        metricDto.setResponseTimeStart(System.nanoTime());
    }

    public static void end(String method, String path){
        String key = method+path;
        MetricDto metricDto = MetricQueueDto.metricMap.getOrDefault(key, new MetricDto());
        metricDto.setResponseTimeEnd(System.nanoTime());
        metricDto.setSuccess(true);
    }

    public static void error(String method, String path){
        String key = method+path;
        MetricDto metricDto = MetricQueueDto.metricMap.getOrDefault(key, new MetricDto());
        metricDto.setResponseTimeEnd(System.nanoTime());
        metricDto.setSuccess(false);
    }

}

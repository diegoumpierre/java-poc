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
        MetricQueueDto.metricMap.put(key,metricDto);
    }

    public static void end(String method, String path){
        String key = method+path;
        MetricDto metricDto = MetricQueueDto.metricMap.getOrDefault(key, new MetricDto());
        metricDto.setResponseTimeEnd(System.nanoTime());
        metricDto.setResponseTime(metricDto.getResponseTimeEnd() - metricDto.getResponseTimeStart());
        metricDto.setSuccess(true);
        MetricQueueDto.metricMap.put(key,metricDto);
    }

    public static void error(String method, String path){
        String key = method+path;
        MetricDto metricDto = MetricQueueDto.metricMap.getOrDefault(key, new MetricDto());
        metricDto.setResponseTimeEnd(System.nanoTime());
        metricDto.setResponseTime(metricDto.getResponseTimeEnd() - metricDto.getResponseTimeStart());
        metricDto.setSuccess(false);
        MetricQueueDto.metricMap.put(key,metricDto);
    }

}

package com.poc.observability.dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class MetricCalculationDto {

    private int countSuccess;
    private int contFail;
    private long bestResponseTime = Integer.MAX_VALUE;
    private long worstResponseTime = Integer.MIN_VALUE;
    private long totalResponseTime;
    private long totalRequests;
    private long averageResponseTime;


    public static MetricCalculationDto buildMetric() {
        MetricCalculationDto metricCalculationDto = new MetricCalculationDto();

        Set<String> metricKeys = MetricQueueDto.metricMap.keySet();

        for (String key : metricKeys) {
            MetricDto metric = MetricQueueDto.metricMap.get(key);

            //total requests
            metricCalculationDto.totalRequests++;

            //totalResponseTime
            metricCalculationDto.totalResponseTime = metricCalculationDto.totalResponseTime + metric.getResponseTime();

            //success or fail
            if (metric.isSuccess()) {
                metricCalculationDto.countSuccess++;
            } else {
                metricCalculationDto.contFail++;
            }

            //bestResponseTime - the short one
            if (metricCalculationDto.bestResponseTime > metric.getResponseTime()) {
                metricCalculationDto.bestResponseTime = metric.getResponseTime();
            }

            //worstResponseTime - the long one
            if (metricCalculationDto.worstResponseTime < metric.getResponseTime()) {
                metricCalculationDto.worstResponseTime = metric.getResponseTime();
            }
        }

        metricCalculationDto.averageResponseTime = metricCalculationDto.totalResponseTime / metricCalculationDto.totalRequests;
        return metricCalculationDto;
    }

}

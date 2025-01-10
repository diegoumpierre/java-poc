package com.poc.observability.service;

import com.poc.observability.dto.MetricCalculationDto;

public class MetricPrintService {

    public static void printConsole(MetricCalculationDto metricCalculationDto){
        System.out.println("Count Success: "+metricCalculationDto.getCountSuccess());
        System.out.println("Cont Fail: "+metricCalculationDto.getContFail());
        System.out.println("Total Response Time: "+metricCalculationDto.getTotalResponseTime());
        System.out.println("Total Requests: "+metricCalculationDto.getTotalRequests());
        System.out.println("Average Response Time: "+metricCalculationDto.getAverageResponseTime());
        System.out.println("Best Response Time: "+metricCalculationDto.getBestResponseTime());
        System.out.println("Worst Response Time = "+metricCalculationDto.getWorstResponseTime());
    }

}

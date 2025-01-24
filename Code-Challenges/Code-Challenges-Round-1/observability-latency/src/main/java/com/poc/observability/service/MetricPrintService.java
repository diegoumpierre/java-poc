package com.poc.observability.service;

import com.poc.observability.dto.MetricCalculationDto;

public class MetricPrintService {

    public static void printConsole(MetricCalculationDto metricCalculationDto) {
        System.out.println("Total Requests: " + metricCalculationDto.getTotalRequests());
        System.out.print("Count Success: " + metricCalculationDto.getCountSuccess());
        System.out.println("    |   Cont Fail: " + metricCalculationDto.getContFail());
        // Convert nanoTime to milliseconds
        long milliTime = metricCalculationDto.getTotalResponseTime() / 1_000_000;
        System.out.println("Total Response Time: " + milliTime+" ms");
        milliTime = metricCalculationDto.getAverageResponseTime() / 1_000_000;
        System.out.println("Average Response Time: " + milliTime+" ms");
        milliTime = metricCalculationDto.getBestResponseTime() / 1_000_000;
        if(milliTime == 0){
            System.out.println("Best Response Time: " + metricCalculationDto.getBestResponseTime()+" nano");
        }else{
            System.out.println("Best Response Time: " + milliTime+" ms");
        }
        milliTime = metricCalculationDto.getWorstResponseTime() / 1_000_000;
        System.out.println("Worst Response Time(ms) = " + milliTime+" ms");
    }

}

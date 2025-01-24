package com.poc.observability.service;

import com.poc.observability.dto.MetricCalculationDto;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
class ObservabilityRegisterTest {

    @Test
    void start() throws InterruptedException {

        String path, method;
        for (int i = 0; i < 100; i++) {
            path = "path" + i;
            method = "method" + i;
            ObservabilityRegister.start(path, method); //need this in any method
            int randomNumber = (int) (Math.random() * 20);
            sleep(randomNumber);
            if(randomNumber > 10){
                ObservabilityRegister.error(path, method); //need this in any method
            }else{
                ObservabilityRegister.end(path, method); //need this in any method
            }
        }
        MetricPrintService.printConsole(MetricCalculationDto.buildMetric());

    }
}
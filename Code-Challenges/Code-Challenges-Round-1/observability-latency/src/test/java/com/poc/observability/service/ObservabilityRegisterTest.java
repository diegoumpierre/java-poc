package com.poc.observability.service;

import com.poc.observability.dto.MetricCalculationDto;

import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;


class ObservabilityRegisterTest {


    @Test
    void fullTest() throws Exception {

        String path, method;
        for (int i = 0; i < 100; i++) {
            path = "path" + i;
            method = "method" + i;
            ObservabilityRegister.start(path, method);
            int randomNumber = (int) (Math.random() * 20);
            sleep(randomNumber);
            if(randomNumber > 10){
                ObservabilityRegister.error(path, method);
            }else{
                ObservabilityRegister.end(path, method);
            }
        }
        MetricPrintService.printConsole(MetricCalculationDto.buildMetric());

    }

}
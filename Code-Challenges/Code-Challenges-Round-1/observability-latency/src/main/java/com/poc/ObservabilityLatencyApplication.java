package com.poc;

import com.poc.observability.service.ScheduleExecuterService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */

@SpringBootApplication
public class ObservabilityLatencyApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ObservabilityLatencyApplication.class, args);
        ScheduleExecuterService.run();
    }

}
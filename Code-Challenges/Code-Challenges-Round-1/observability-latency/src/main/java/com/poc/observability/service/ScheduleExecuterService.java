package com.poc.observability.service;

import com.poc.observability.dto.MetricCalculationDto;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduleExecuterService {

    public static void run() throws InterruptedException {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

        Runnable printMetrics = () -> {
            MetricPrintService.printConsole(MetricCalculationDto.buildMetric());
        };

        // init Delay = 5, repeat the task every 1 second
        ScheduledFuture<?> scheduledFuture = service.scheduleAtFixedRate(printMetrics, 5, 1, TimeUnit.SECONDS);
        int count = 0;
        while (true) {
            if (count == 5) {
                scheduledFuture.cancel(true);
                service.shutdown();
                break;
            }
        }

    }

}

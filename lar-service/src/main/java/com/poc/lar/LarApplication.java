package com.poc.lar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"com.poc.lar", "com.poc.shared"})
public class LarApplication {
    public static void main(String[] args) {
        SpringApplication.run(LarApplication.class, args);
    }
}

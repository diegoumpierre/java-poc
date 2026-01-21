package com.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Authentication and User Management Service
 * Port: 8091
 * Database: auth_db
 * Bucket: auth-files
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.poc.auth.storage.client", "com.poc.auth.client"})
@EnableScheduling
@EnableAsync
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}

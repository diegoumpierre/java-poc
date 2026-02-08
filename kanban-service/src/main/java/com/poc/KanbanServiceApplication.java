package com.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Kanban Board Management Service
 * Port: 8081
 * Database: kanban_db
 * Bucket: kanban-files
 * Topics: kanban.*
 */
@SpringBootApplication(scanBasePackages = {
        "com.poc.kanban",
        "com.poc.shared"
})
@EnableFeignClients(basePackages = "com.poc.kanban.storage.client")
public class KanbanServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KanbanServiceApplication.class, args);
    }
}

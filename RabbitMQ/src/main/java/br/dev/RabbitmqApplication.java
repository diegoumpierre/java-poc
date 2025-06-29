package br.dev;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitmqApplication implements CommandLineRunner {

    private final MessageSender sender;

    public RabbitmqApplication(MessageSender sender) {
        this.sender = sender;
    }

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqApplication.class, args);
    }

    @Override
    public void run(String... args) {
        sender.sendMessage("Hello from RabbitMQ!");
    }
}
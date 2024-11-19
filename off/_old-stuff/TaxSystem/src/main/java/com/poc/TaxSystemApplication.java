package com.poc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class TaxSystemApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TaxSystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Running ....");
	}
}
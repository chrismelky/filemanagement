package com.softalanta.batchlay;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@EnableBatchProcessing
public class BatchLayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchLayApplication.class, args);
	}

}

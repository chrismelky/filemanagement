package com.softalanta.batchlay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
public class BatchLayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchLayApplication.class, args);
	}

}

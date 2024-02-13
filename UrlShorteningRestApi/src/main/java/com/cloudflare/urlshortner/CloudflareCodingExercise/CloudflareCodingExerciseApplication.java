package com.cloudflare.urlshortner.CloudflareCodingExercise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.cloudflare.*")
@EnableCaching
@EnableAsync
public class CloudflareCodingExerciseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudflareCodingExerciseApplication.class, args);
	}

}

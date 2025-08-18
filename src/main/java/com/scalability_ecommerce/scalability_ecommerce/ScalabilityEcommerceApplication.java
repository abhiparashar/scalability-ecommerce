package com.scalability_ecommerce.scalability_ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class ScalabilityEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScalabilityEcommerceApplication.class, args);
	}

}

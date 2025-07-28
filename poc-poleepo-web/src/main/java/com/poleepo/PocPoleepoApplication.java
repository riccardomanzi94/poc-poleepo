package com.poleepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PocPoleepoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocPoleepoApplication.class, args);
	}


}

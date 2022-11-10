package com.example.handlemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HandleManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HandleManagerApplication.class, args);
	}

}

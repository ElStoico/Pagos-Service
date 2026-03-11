package com.pagosservice.pagosservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PagosserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagosserviceApplication.class, args);
	}

}

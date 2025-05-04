package com.ams.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.ams")
public class AmsGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmsGatewayApplication.class, args);


	}
}

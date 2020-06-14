package com.zbutwialypiernik.flixage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}

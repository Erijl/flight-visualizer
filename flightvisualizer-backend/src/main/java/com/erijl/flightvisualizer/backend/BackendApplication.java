package com.erijl.flightvisualizer.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

}

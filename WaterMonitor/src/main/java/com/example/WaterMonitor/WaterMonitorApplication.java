package com.example.WaterMonitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WaterMonitorApplication {

	@Autowired
	private MyService myService;

	public static void main(String[] args) {
		SpringApplication.run(WaterMonitorApplication.class, args);
	}
}

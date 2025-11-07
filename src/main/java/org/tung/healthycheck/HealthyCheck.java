package org.tung.healthycheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HealthyCheck {

	public static void main(String[] args) {
		SpringApplication.run(HealthyCheck.class, args);
	}

}

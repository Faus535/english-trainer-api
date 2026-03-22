package com.faus535.englishtrainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class EnglishTrainerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnglishTrainerApiApplication.class, args);
	}

}

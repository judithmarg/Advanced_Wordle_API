package com.beproject.wordleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WordleapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordleapiApplication.class, args);
	}

}
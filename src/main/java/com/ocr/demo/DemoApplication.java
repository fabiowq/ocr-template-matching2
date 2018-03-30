package com.ocr.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
	    SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
    public CommandLineRunner runner(ImageService imageService) {
        return args -> {
            imageService.process();
        };
    }


}
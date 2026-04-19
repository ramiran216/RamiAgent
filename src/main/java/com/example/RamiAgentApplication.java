package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:deepseek.properties")
public class RamiAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(RamiAgentApplication.class, args);
    }
}
package com.visakhatourism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VisakhaTourismApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisakhaTourismApplication.class, args);
    }
}

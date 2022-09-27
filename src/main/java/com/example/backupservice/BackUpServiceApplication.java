package com.example.backupservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackUpServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackUpServiceApplication.class, args);
    }

}

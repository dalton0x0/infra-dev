package com.cheridanh.infradev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InfraDevApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfraDevApplication.class, args);
    }

}

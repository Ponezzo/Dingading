package com.mickey.dinggading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DinggadingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DinggadingApplication.class, args);
    }

}

package com.mickey.dinggading;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    //
    @Value("${SPRING_ENV_MODE}")
    private String springEnvMode;

    @GetMapping("/api/hello")
    public String hello() {
        System.out.println(springEnvMode);
        return springEnvMode;
    }
}
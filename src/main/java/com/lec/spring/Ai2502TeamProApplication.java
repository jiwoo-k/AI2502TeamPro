package com.lec.spring;

import com.lec.spring.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SecurityConfig.class)
public class Ai2502TeamProApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ai2502TeamProApplication.class, args);
    }

}

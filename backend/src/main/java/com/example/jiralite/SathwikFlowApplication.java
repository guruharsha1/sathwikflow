package com.example.jiralite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SathwikFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(SathwikFlowApplication.class, args);
    }
}

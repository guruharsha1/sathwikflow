package com.example.jiralite.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI sathwikFlowOpenApi() {
        return new OpenAPI().info(new Info().title("SathwikFlow API").version("v1")
                .description("JWT-protected project, planning, issue, and workflow API."));
    }
}


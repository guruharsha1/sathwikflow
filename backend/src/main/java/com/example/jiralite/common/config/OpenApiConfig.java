package com.example.jiralite.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {
    @Bean
    OpenAPI sathwikFlowOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SathwikFlow API")
                .version("v1")
                .description("Project management API with project RBAC, workflows, issues, comments, and planning."));
    }
}

package com.arjunai.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Arjun AI Project API")
                        .version("1.0.0")
                        .description("Spring Boot REST API for mathematical operations")
                        .contact(new Contact()
                                .name("Arjun Raju")
                                .email("arjun@example.com")
                                .url("https://github.com/arjunraju"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
} 
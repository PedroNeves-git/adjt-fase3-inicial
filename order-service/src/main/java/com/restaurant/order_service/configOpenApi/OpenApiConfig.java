package com.restaurant.order_service.configOpenApi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        SecurityScheme jwt = new SecurityScheme()
                .name(BEARER_SCHEME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("Customer-facing order management for the Restaurant Phase 3 challenge.")
                        .version("v1"))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME, jwt))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}

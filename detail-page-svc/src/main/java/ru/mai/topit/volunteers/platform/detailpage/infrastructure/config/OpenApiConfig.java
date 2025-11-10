package ru.mai.topit.volunteers.platform.detailpage.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI (Swagger) для документации API.
 * Настраивает метаданные API и делает его доступным через Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Detail Page API")
                        .description("API для управления детальными страницами с информацией")
                        .version("v1"));
    }
}




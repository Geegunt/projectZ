package ru.mai.topit.volunteers.platform.detailpage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Главный класс Spring Boot приложения для микросервиса детальных страниц.
 * Запускает приложение и инициализирует все Spring компоненты.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class DetailPageApplication {

    public static void main(String[] args) {
        SpringApplication.run(DetailPageApplication.class, args);
    }
}




package ru.mai.topit.volunteers.platform.eventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Главный класс Spring Boot приложения для микросервиса событий.
 * Запускает приложение и инициализирует все Spring компоненты.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class EventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }
}




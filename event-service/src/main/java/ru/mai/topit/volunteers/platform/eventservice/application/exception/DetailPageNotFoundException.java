package ru.mai.topit.volunteers.platform.eventservice.application.exception;

/**
 * Исключение, выбрасываемое когда детальная страница не найдена
 */
public class DetailPageNotFoundException extends RuntimeException {
    public DetailPageNotFoundException(Long id) {
        super("Detail page with id " + id + " not found");
    }

    public DetailPageNotFoundException(String message) {
        super(message);
    }
}




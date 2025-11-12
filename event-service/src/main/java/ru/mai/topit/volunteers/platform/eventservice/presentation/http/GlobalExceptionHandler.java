package ru.mai.topit.volunteers.platform.eventservice.presentation.http;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mai.topit.volunteers.platform.eventservice.application.exception.DetailPageNotFoundException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Перехватывает исключения и возвращает структурированные ответы об ошибках.
 */
@RestControllerAdvice
@Hidden
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение, когда детальная страница не найдена.
     */
    @ExceptionHandler(DetailPageNotFoundException.class)
    public ResponseEntity<Object> handleDetailPageNotFound(
            DetailPageNotFoundException ex,
            HttpServletRequest request) {
        return error(request, HttpStatus.NOT_FOUND, "DETAIL_PAGE_NOT_FOUND", ex.getMessage(), null);
    }

    /**
     * Обрабатывает ошибки валидации при проверке DTO.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, Object> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            details.put(error.getField(), error.getDefaultMessage());
        });
        return error(request, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", 
                "Validation failed", details);
    }

    /**
     * Обрабатывает ошибки валидации при нарушении ограничений.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        Map<String, Object> details = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            details.put(violation.getPropertyPath().toString(), violation.getMessage());
        });
        return error(request, HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION",
                "Constraint violation", details);
    }

    /**
     * Обрабатывает неверные аргументы.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        return error(request, HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), null);
    }

    /**
     * Обрабатывает все остальные неожиданные исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        return error(request, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Internal server error", null);
    }

    /**
     * Создает структурированный ответ об ошибке.
     *
     * @param request HTTP запрос
     * @param status HTTP статус код
     * @param code код ошибки
     * @param message сообщение об ошибке
     * @param details дополнительные детали ошибки
     * @return ResponseEntity с информацией об ошибке
     */
    private ResponseEntity<Object> error(
            HttpServletRequest request,
            HttpStatus status,
            String code,
            String message,
            Map<String, Object> details) {
        Map<String, Object> safeDetails = details != null ? details : new HashMap<>();
        
        log.error("Request failed: code={}, status={}, path={}, message={}, details={}",
                code,
                status.value(),
                request != null ? request.getRequestURI() : null,
                message,
                safeDetails);

        Map<String, Object> body = new HashMap<>();
        body.put("errorId", UUID.randomUUID().toString());
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", status.value());
        body.put("code", code);
        body.put("message", message);
        body.put("path", request != null ? request.getRequestURI() : null);
        
        if (!safeDetails.isEmpty()) {
            body.put("details", safeDetails);
        }
        
        return ResponseEntity.status(status).body(body);
    }
}




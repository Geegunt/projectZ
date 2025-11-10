package ru.mai.topit.volunteers.platform.detailpage.presentation.http.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * DTO (Data Transfer Objects) для работы с детальными страницами.
 * Используются для передачи данных между клиентом и сервером.
 */
public class DetailPageDtos {

    /**
     * DTO для ответа с данными детальной страницы.
     * Содержит все поля страницы для отображения.
     */
    public record DetailPageResponse(
            Long id,
            String title,
            String description,
            String content,
            String pageType,
            Map<String, Object> metadata,
            String imageUrl,
            String status,
            Long authorId,
            Long viewsCount,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            OffsetDateTime publishedAt
    ) {
    }

    /**
     * DTO для запроса на создание новой детальной страницы.
     * Валидируется с помощью Jakarta Validation.
     */
    public record DetailPageCreateRequest(
            @NotBlank(message = "Title is required")
            @Size(max = 500, message = "Title must not exceed 500 characters")
            String title,

            @Size(max = 2000, message = "Description must not exceed 2000 characters")
            String description,

            String content,

            @NotBlank(message = "Page type is required")
            @Size(max = 50, message = "Page type must not exceed 50 characters")
            String pageType,

            Map<String, Object> metadata,

            String imageUrl,

            @Size(max = 20, message = "Status must not exceed 20 characters")
            String status
    ) {
    }

    /**
     * DTO для запроса на обновление существующей детальной страницы.
     * Все поля опциональны - обновляются только переданные поля.
     */
    public record DetailPageUpdateRequest(
            @Size(max = 500, message = "Title must not exceed 500 characters")
            String title,

            @Size(max = 2000, message = "Description must not exceed 2000 characters")
            String description,

            String content,

            @Size(max = 50, message = "Page type must not exceed 50 characters")
            String pageType,

            Map<String, Object> metadata,

            String imageUrl,

            @Size(max = 20, message = "Status must not exceed 20 characters")
            String status
    ) {
    }
}




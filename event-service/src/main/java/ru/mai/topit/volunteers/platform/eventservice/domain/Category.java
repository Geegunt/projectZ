package ru.mai.topit.volunteers.platform.eventservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

/**
 * Сущность, представляющая категорию для классификации событий.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories", schema = "event_service")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Название категории
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Описание категории
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Цвет категории в HEX формате (#RRGGBB)
     */
    @Size(max = 7)
    @Column(name = "color", length = 7)
    private String color;

    /**
     * Иконка категории
     */
    @Size(max = 50)
    @Column(name = "icon", length = 50)
    private String icon;

    /**
     * Активна ли категория
     */
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * Порядок сортировки
     */
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    /**
     * Дата и время создания
     */
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Дата и время последнего обновления
     */
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
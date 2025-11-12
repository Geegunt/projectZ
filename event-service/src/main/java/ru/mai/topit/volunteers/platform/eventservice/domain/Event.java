package ru.mai.topit.volunteers.platform.eventservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Сущность, представляющая событие в системе.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events", schema = "event_service")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Заголовок события
     */
    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * Краткое описание события
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Полное содержимое события (HTML/Markdown)
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * Категория события
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, referencedColumnName = "id")
    private Category category;

    /**
     * URL изображения или обложки
     */
    @Column(name = "image_url", length = Integer.MAX_VALUE)
    private String imageUrl;

    /**
     * Статус события (draft, published, cancelled, completed)
     */
    @NotBlank
    @ColumnDefault("'draft'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * Тип мероприятия (online, offline, hybrid)
     */
    @NotBlank
    @ColumnDefault("'online'")
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    /**
     * Максимальное количество участников
     */
    @Column(name = "max_participants")
    private Integer maxParticipants;

    /**
     * Текущее количество участников
     */
    @ColumnDefault("0")
    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants;

    /**
     * Возрастное ограничение (0+ до 18+)
     */
    @Column(name = "age_restriction")
    private Integer ageRestriction;

    /**
     * Название места проведения
     */
    @Size(max = 200)
    @Column(name = "location_name", length = 200)
    private String locationName;

    /**
     * Адрес места проведения
     */
    @Column(name = "location_address", columnDefinition = "TEXT")
    private String locationAddress;

    /**
     * Широта места проведения
     */
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    /**
     * Долгота места проведения
     */
    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    /**
     * URL для онлайн участия
     */
    @Column(name = "online_url", length = Integer.MAX_VALUE)
    private String onlineUrl;

    /**
     * Дата и время начала события
     */
    @NotNull
    @Column(name = "start_date", nullable = false)
    private OffsetDateTime startDate;

    /**
     * Дата и время окончания события
     */
    @NotNull
    @Column(name = "end_date", nullable = false)
    private OffsetDateTime endDate;

    /**
     * Дедлайн регистрации на событие
     */
    @Column(name = "registration_deadline")
    private OffsetDateTime registrationDeadline;

    /**
     * ID автора/организатора события
     */
    @NotNull
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    /**
     * Количество просмотров события
     */
    @ColumnDefault("0")
    @Column(name = "views_count", nullable = false)
    private Long viewsCount;

    /**
     * Является ли событие рекомендуемым
     */
    @ColumnDefault("false")
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured;

    /**
     * Теги события
     */
    @ElementCollection
    @CollectionTable(name = "event_tags", schema = "event_service", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "tag")
    private List<String> tags;

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

    /**
     * Дата и время публикации
     */
    @Column(name = "published_at")
    private OffsetDateTime publishedAt;
}
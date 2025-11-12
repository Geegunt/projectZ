package ru.mai.topit.volunteers.platform.eventservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Сущность, представляющая заявку на участие в событии.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_applications", schema = "event_service")
public class EventApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Событие, на которое подана заявка
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, referencedColumnName = "id")
    private Event event;

    /**
     * ID пользователя, подавшего заявку
     */
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Статус заявки (pending, approved, rejected, cancelled)
     */
    @ColumnDefault("'pending'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * Дата подачи заявки
     */
    @ColumnDefault("now()")
    @Column(name = "application_date", nullable = false)
    private OffsetDateTime applicationDate;

    /**
     * Контактная информация заявителя в JSON
     */
    @Column(name = "contact_info")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> contactInfo;

    /**
     * Сообщение от заявителя
     */
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    /**
     * ID пользователя, который рассмотрел заявку
     */
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    /**
     * Дата рассмотрения заявки
     */
    @Column(name = "review_date")
    private OffsetDateTime reviewDate;

    /**
     * Комментарий при рассмотрении
     */
    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

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
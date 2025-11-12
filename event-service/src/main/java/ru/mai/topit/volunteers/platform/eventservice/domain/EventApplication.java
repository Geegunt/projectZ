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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, referencedColumnName = "id")
    private Event event;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ColumnDefault("'pending'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ColumnDefault("now()")
    @Column(name = "application_date", nullable = false)
    private OffsetDateTime applicationDate;

    @Column(name = "contact_info")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> contactInfo;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "review_date")
    private OffsetDateTime reviewDate;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public static EventApplication apply(Event event, Long userId) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (!event.canRegister()) {
            throw new IllegalStateException("Cannot apply to this event: event is not available for registration");
        }

        return EventApplication.builder()
                .event(event)
                .userId(userId)
                .status(EventApplicationStatus.PENDING.getValue())
                .applicationDate(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    public void approve(Long reviewerId) {
        if (reviewerId == null || reviewerId <= 0) {
            throw new IllegalArgumentException("Reviewer ID must be positive");
        }
        if (!EventApplicationStatus.PENDING.getValue().equals(this.status)) {
            throw new IllegalStateException("Only pending applications can be approved");
        }
        this.status = EventApplicationStatus.APPROVED.getValue();
        this.reviewedBy = reviewerId;
        this.reviewDate = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public void reject(Long reviewerId, String comment) {
        if (reviewerId == null || reviewerId <= 0) {
            throw new IllegalArgumentException("Reviewer ID must be positive");
        }
        if (!EventApplicationStatus.PENDING.getValue().equals(this.status)) {
            throw new IllegalStateException("Only pending applications can be rejected");
        }
        this.status = EventApplicationStatus.REJECTED.getValue();
        this.reviewedBy = reviewerId;
        this.reviewDate = OffsetDateTime.now();
        this.reviewComment = comment;
        this.updatedAt = OffsetDateTime.now();
    }

    public void cancel() {
        if (EventApplicationStatus.CANCELLED.getValue().equals(this.status)) {
            throw new IllegalStateException("Application is already cancelled");
        }
        this.status = EventApplicationStatus.CANCELLED.getValue();
        this.updatedAt = OffsetDateTime.now();
    }

    public boolean isPending() {
        return EventApplicationStatus.PENDING.getValue().equals(this.status);
    }

    public boolean isApproved() {
        return EventApplicationStatus.APPROVED.getValue().equals(this.status);
    }
}
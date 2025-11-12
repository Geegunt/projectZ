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
import ru.mai.topit.volunteers.platform.eventservice.domain.vo.EventSchedule;
import ru.mai.topit.volunteers.platform.eventservice.domain.vo.Location;
import ru.mai.topit.volunteers.platform.eventservice.domain.vo.ParticipantLimits;

import java.time.OffsetDateTime;
import java.util.List;

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

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, referencedColumnName = "id")
    private Category category;

    @Column(name = "image_url", length = Integer.MAX_VALUE)
    private String imageUrl;

    @NotBlank
    @ColumnDefault("'draft'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @NotBlank
    @ColumnDefault("'online'")
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "age_restriction")
    private Integer ageRestriction;

    @Column(name = "online_url", length = Integer.MAX_VALUE)
    private String onlineUrl;

    @NotNull
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @ColumnDefault("0")
    @Column(name = "views_count", nullable = false)
    private Long viewsCount;

    @ColumnDefault("false")
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured;

    @ElementCollection
    @CollectionTable(name = "event_tags", schema = "event_service", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "tag")
    private List<String> tags;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "location_name", length = 200)),
        @AttributeOverride(name = "address", column = @Column(name = "location_address", columnDefinition = "TEXT")),
        @AttributeOverride(name = "latitude", column = @Column(name = "latitude", precision = 10, scale = 8)),
        @AttributeOverride(name = "longitude", column = @Column(name = "longitude", precision = 11, scale = 8))
    })
    private Location location;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "maxParticipants", column = @Column(name = "max_participants")),
        @AttributeOverride(name = "currentParticipants", column = @Column(name = "current_participants", nullable = false))
    })
    private ParticipantLimits participantLimits;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "startDate", column = @Column(name = "start_date", nullable = false)),
        @AttributeOverride(name = "endDate", column = @Column(name = "end_date", nullable = false)),
        @AttributeOverride(name = "registrationDeadline", column = @Column(name = "registration_deadline"))
    })
    private EventSchedule schedule;

    public static Event create(Long authorId, String title, Category category, EventSchedule schedule) {
        if (authorId == null || authorId <= 0) {
            throw new IllegalArgumentException("Author ID must be positive");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Title cannot exceed 200 characters");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule cannot be null");
        }
        if (!schedule.isValid()) {
            throw new IllegalArgumentException("Schedule is invalid");
        }

        return Event.builder()
                .title(title)
                .category(category)
                .authorId(authorId)
                .status(EventStatus.DRAFT.getValue())
                .eventType(EventType.ONLINE.getValue())
                .schedule(schedule)
                .participantLimits(ParticipantLimits.builder()
                        .maxParticipants(null)
                        .currentParticipants(0)
                        .build())
                .viewsCount(0L)
                .isFeatured(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    public void publish() {
        if (EventStatus.DRAFT.getValue().equals(this.status)) {
            this.status = EventStatus.PUBLISHED.getValue();
            this.publishedAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
        }
    }

    public void cancel() {
        if (!EventStatus.COMPLETED.getValue().equals(this.status)) {
            this.status = EventStatus.CANCELLED.getValue();
            this.updatedAt = OffsetDateTime.now();
        }
    }

    public void complete() {
        this.status = EventStatus.COMPLETED.getValue();
        this.updatedAt = OffsetDateTime.now();
    }

    public boolean canRegister() {
        return schedule.isRegistrationOpen() && 
               EventStatus.PUBLISHED.getValue().equals(this.status);
    }

    public void incrementParticipantCount() {
        if (!participantLimits.hasAvailableSlots()) {
            throw new IllegalStateException("Event is full, no available slots");
        }
        this.participantLimits = this.participantLimits.incrementParticipants();
        this.updatedAt = OffsetDateTime.now();
    }

    public void decrementParticipantCount() {
        this.participantLimits = this.participantLimits.decrementParticipants();
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateEventType(EventType eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        this.eventType = eventType.getValue();
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateLocation(Location location) {
        if (location != null && !location.isValid()) {
            throw new IllegalArgumentException("Location is invalid");
        }
        this.location = location;
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateSchedule(EventSchedule schedule) {
        if (schedule == null || !schedule.isValid()) {
            throw new IllegalArgumentException("Schedule is invalid");
        }
        this.schedule = schedule;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setParticipantLimits(int maxParticipants) {
        if (maxParticipants <= 0) {
            throw new IllegalArgumentException("Max participants must be positive");
        }
        if (maxParticipants < participantLimits.getCurrentParticipants()) {
            throw new IllegalArgumentException("Max participants cannot be less than current participants");
        }
        this.participantLimits = ParticipantLimits.builder()
                .maxParticipants(maxParticipants)
                .currentParticipants(participantLimits.getCurrentParticipants())
                .build();
        this.updatedAt = OffsetDateTime.now();
    }
}
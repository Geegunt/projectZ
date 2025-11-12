import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_applications", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "eventId"})
})
public class EventApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // ID волонтера из User Service

    @Column(nullable = false)
    private Long eventId; // ID нашего мероприятия

    // В реальном проекте здесь будет связь @ManyToOne с Event
    // @ManyToOne
    // @JoinColumn(name = "event_id")
    // private Event event;

    // TODO: Добавить Enum для статуса (PENDING, APPROVED, REJECTED)
    // @Enumerated(EnumType.STRING)
    // private ApplicationStatus status;

    private Boolean attended = false; //

    private LocalDateTime applicationTime = LocalDateTime.now();

    // Геттеры, сеттеры...
}
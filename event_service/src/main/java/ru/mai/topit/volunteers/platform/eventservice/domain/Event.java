import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private Integer durationHours; // [cite: 156]

    private String location;

    @Column(nullable = false)
    private Integer participantLimit; // [cite: 156]

    private String chatLink; // [cite: 156]
    private String imageUrl;

    @Column(nullable = false)
    private Long organizerId; // ID из User Service

    // TODO: Добавить связь с категорией
    // @ManyToOne
    // @JoinColumn(name = "category_id")
    // private EventCategory category;

    // TODO: Добавить Enum для статуса
    // @Enumerated(EnumType.STRING)
    // private EventStatus status;

    // Геттеры, сеттеры, equals, hashCode...
}
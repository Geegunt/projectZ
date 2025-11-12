package ru.mai.topit.volunteers.platform.eventservice.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mai.topit.volunteers.platform.eventservice.domain.Event;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Репозиторий для работы с событиями.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Найти опубликованные события с пагинацией.
     */
    Page<Event> findByStatusOrderByStartDateAsc(String status, Pageable pageable);

    /**
     * Найти опубликованные события по категории.
     */
    Page<Event> findByStatusAndCategoryIdOrderByStartDateAsc(String status, Long categoryId, Pageable pageable);

    /**
     * Найти рекомендуемые события.
     */
    List<Event> findByStatusAndIsFeaturedTrueOrderByStartDateAsc(String status);

    /**
     * Найти события автора.
     */
    Page<Event> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    /**
     * Найти события по типу мероприятия.
     */
    Page<Event> findByStatusAndEventTypeOrderByStartDateAsc(String status, String eventType, Pageable pageable);

    /**
     * Поиск событий по названию или описанию.
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY e.startDate ASC")
    Page<Event> searchPublishedEvents(@Param("status") String status, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Найти предстоящие события.
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.startDate > :now ORDER BY e.startDate ASC")
    List<Event> findUpcomingEvents(@Param("status") String status, @Param("now") OffsetDateTime now);

    /**
     * Найти события в указанном временном диапазоне.
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "e.startDate >= :startDate AND e.endDate <= :endDate " +
           "ORDER BY e.startDate ASC")
    List<Event> findEventsInDateRange(@Param("status") String status, 
                                     @Param("startDate") OffsetDateTime startDate, 
                                     @Param("endDate") OffsetDateTime endDate);

    /**
     * Найти события по тегам.
     */
    @Query("SELECT DISTINCT e FROM Event e JOIN e.tags t WHERE e.status = :status AND t IN :tags ORDER BY e.startDate ASC")
    List<Event> findEventsByTags(@Param("status") String status, @Param("tags") List<String> tags);

    /**
     * Найти события рядом с указанными координатами.
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "e.latitude IS NOT NULL AND e.longitude IS NOT NULL AND " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * " +
           "cos(radians(e.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
           "sin(radians(e.latitude)))) <= :radius " +
           "ORDER BY e.startDate ASC")
    List<Event> findNearbyEvents(@Param("status") String status, 
                               @Param("latitude") Double latitude, 
                               @Param("longitude") Double longitude, 
                               @Param("radius") Double radius);
}
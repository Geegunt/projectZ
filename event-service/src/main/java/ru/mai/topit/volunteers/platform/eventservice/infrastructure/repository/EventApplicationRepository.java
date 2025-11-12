package ru.mai.topit.volunteers.platform.eventservice.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mai.topit.volunteers.platform.eventservice.domain.EventApplication;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с заявками на участие в событиях.
 */
@Repository
public interface EventApplicationRepository extends JpaRepository<EventApplication, Long> {

    /**
     * Найти заявку по событию и пользователю.
     */
    Optional<EventApplication> findByEventIdAndUserId(Long eventId, Long userId);

    /**
     * Найти все заявки на событие с пагинацией.
     */
    Page<EventApplication> findByEventIdOrderByApplicationDateDesc(Long eventId, Pageable pageable);

    /**
     * Найти все заявки пользователя с пагинацией.
     */
    Page<EventApplication> findByUserIdOrderByApplicationDateDesc(Long userId, Pageable pageable);

    /**
     * Найти заявки по статусу.
     */
    Page<EventApplication> findByStatusOrderByApplicationDateDesc(String status, Pageable pageable);

    /**
     * Найти заявки на событие по статусу.
     */
    List<EventApplication> findByEventIdAndStatus(Long eventId, String status);

    /**
     * Подсчитать количество заявок на событие по статусу.
     */
    @Query("SELECT COUNT(ea) FROM EventApplication ea WHERE ea.event.id = :eventId AND ea.status = :status")
    long countByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") String status);

    /**
     * Проверить, подал ли пользователь заявку на событие.
     */
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    /**
     * Найти заявки, ожидающие рассмотрения.
     */
    @Query("SELECT ea FROM EventApplication ea WHERE ea.status = 'pending' ORDER BY ea.applicationDate ASC")
    List<EventApplication> findPendingApplications();

    /**
     * Найти заявки, которые нужно рассмотреть конкретному пользователю.
     */
    @Query("SELECT ea FROM EventApplication ea WHERE ea.reviewedBy = :reviewedBy AND ea.status IN ('pending', 'approved', 'rejected') ORDER BY ea.reviewDate DESC")
    Page<EventApplication> findByReviewedBy(@Param("reviewedBy") Long reviewedBy, Pageable pageable);

    /**
     * Найти активные заявки пользователя (не отмененные и не отклоненные).
     */
    @Query("SELECT ea FROM EventApplication ea WHERE ea.userId = :userId AND ea.status IN ('pending', 'approved') ORDER BY ea.applicationDate DESC")
    List<EventApplication> findActiveApplicationsByUser(@Param("userId") Long userId);
}
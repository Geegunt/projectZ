package ru.mai.topit.volunteers.platform.eventservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mai.topit.volunteers.platform.eventservice.domain.Category;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с категориями событий.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Найти активные категории, отсортированные по порядку сортировки.
     */
    List<Category> findByIsActiveTrueOrderBySortOrderAsc();

    /**
     * Найти категорию по имени.
     */
    Optional<Category> findByName(String name);

    /**
     * Проверить существование категории с таким именем.
     */
    boolean existsByName(String name);

    /**
     * Найти категории по части имени (для поиска).
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY c.sortOrder ASC")
    List<Category> findActiveByNameContaining(@Param("name") String name);
}
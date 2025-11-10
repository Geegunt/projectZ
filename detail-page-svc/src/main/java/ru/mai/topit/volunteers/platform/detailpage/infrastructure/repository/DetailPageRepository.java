package ru.mai.topit.volunteers.platform.detailpage.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mai.topit.volunteers.platform.detailpage.domain.DetailPage;

import java.util.Optional;

/**
 * Репозиторий для работы с детальными страницами в базе данных.
 * Использует Spring Data JPA для автоматической генерации SQL-запросов.
 */
public interface DetailPageRepository extends JpaRepository<DetailPage, Long> {

    /**
     * Находит страницу по ID (стандартный метод JpaRepository).
     */
    Optional<DetailPage> findById(Long id);

    /**
     * Находит все страницы определенного типа с пагинацией.
     *
     * @param pageType тип страницы
     * @param pageable параметры пагинации
     * @return страница с результатами
     */
    Page<DetailPage> findByPageType(String pageType, Pageable pageable);

    /**
     * Находит все страницы определенного статуса с пагинацией.
     *
     * @param status статус страницы
     * @param pageable параметры пагинации
     * @return страница с результатами
     */
    Page<DetailPage> findByStatus(String status, Pageable pageable);

    /**
     * Находит все страницы определенного типа и статуса с пагинацией.
     *
     * @param pageType тип страницы
     * @param status статус страницы
     * @param pageable параметры пагинации
     * @return страница с результатами
     */
    Page<DetailPage> findByPageTypeAndStatus(String pageType, String status, Pageable pageable);

    /**
     * Проверяет существование страницы по ID (стандартный метод JpaRepository).
     */
    boolean existsById(Long id);
}




package ru.mai.topit.volunteers.platform.eventservice.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mai.topit.volunteers.platform.eventservice.application.exception.DetailPageNotFoundException;
import ru.mai.topit.volunteers.platform.eventservice.application.mapper.DetailPageMapper;
import ru.mai.topit.volunteers.platform.eventservice.domain.DetailPage;
import ru.mai.topit.volunteers.platform.eventservice.infrastructure.repository.DetailPageRepository;
import ru.mai.topit.volunteers.platform.eventservice.presentation.http.dto.DetailPageDtos;

import java.time.OffsetDateTime;

/**
 * Сервисный слой для работы с детальными страницами.
 * Содержит бизнес-логику для операций CRUD и дополнительных действий.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DetailPageService {

    private final DetailPageRepository detailPageRepository;
    private final DetailPageMapper detailPageMapper;

    /**
     * Получает детальную страницу по ID.
     * Увеличивает счетчик просмотров при каждом запросе.
     *
     * @param id идентификатор страницы
     * @return DTO с данными страницы
     * @throws DetailPageNotFoundException если страница не найдена
     */
    @Transactional
    public DetailPageDtos.DetailPageResponse getDetailPageById(Long id) {
        log.debug("Getting detail page with id: {}", id);
        
        DetailPage page = detailPageRepository.findById(id)
                .orElseThrow(() -> new DetailPageNotFoundException(id));

        // Увеличиваем счетчик просмотров
        page.setViewsCount(page.getViewsCount() + 1);
        detailPageRepository.save(page);

        return detailPageMapper.toDto(page);
    }

    /**
     * Получает все детальные страницы с пагинацией.
     * Можно фильтровать по типу страницы и статусу.
     *
     * @param pageType тип страницы (опционально)
     * @param status статус страницы (опционально)
     * @param pageable параметры пагинации
     * @return страница с результатами
     */
    @Transactional(readOnly = true)
    public Page<DetailPageDtos.DetailPageResponse> getAllDetailPages(
            String pageType,
            String status,
            Pageable pageable) {
        log.debug("Getting all detail pages with filters: pageType={}, status={}", pageType, status);

        Page<DetailPage> pages;
        if (pageType != null && status != null) {
            pages = detailPageRepository.findByPageTypeAndStatus(pageType, status, pageable);
        } else if (pageType != null) {
            pages = detailPageRepository.findByPageType(pageType, pageable);
        } else if (status != null) {
            pages = detailPageRepository.findByStatus(status, pageable);
        } else {
            pages = detailPageRepository.findAll(pageable);
        }

        return pages.map(detailPageMapper::toDto);
    }

    /**
     * Создает новую детальную страницу.
     *
     * @param request данные для создания страницы
     * @param authorId ID автора (может быть null)
     * @return созданная страница
     */
    @Transactional
    public DetailPageDtos.DetailPageResponse createDetailPage(
            DetailPageDtos.DetailPageCreateRequest request,
            Long authorId) {
        log.debug("Creating new detail page with title: {}", request.title());

        DetailPage page = detailPageMapper.toEntity(request);
        page.setAuthorId(authorId);
        page.setCreatedAt(OffsetDateTime.now());
        page.setUpdatedAt(OffsetDateTime.now());

        DetailPage saved = detailPageRepository.save(page);
        log.info("Detail page created with id: {}", saved.getId());

        return detailPageMapper.toDto(saved);
    }

    /**
     * Обновляет существующую детальную страницу.
     *
     * @param id идентификатор страницы
     * @param request данные для обновления
     * @return обновленная страница
     * @throws DetailPageNotFoundException если страница не найдена
     */
    @Transactional
    public DetailPageDtos.DetailPageResponse updateDetailPage(
            Long id,
            DetailPageDtos.DetailPageUpdateRequest request) {
        log.debug("Updating detail page with id: {}", id);

        DetailPage page = detailPageRepository.findById(id)
                .orElseThrow(() -> new DetailPageNotFoundException(id));

        detailPageMapper.updateEntityFromRequest(request, page);
        page.setUpdatedAt(OffsetDateTime.now());

        // Если статус меняется на "published" и publishedAt еще не установлен, устанавливаем его
        if ("published".equals(request.status()) && page.getPublishedAt() == null) {
            page.setPublishedAt(OffsetDateTime.now());
        }

        DetailPage saved = detailPageRepository.save(page);
        log.info("Detail page updated with id: {}", saved.getId());

        return detailPageMapper.toDto(saved);
    }

    /**
     * Удаляет детальную страницу.
     *
     * @param id идентификатор страницы
     * @throws DetailPageNotFoundException если страница не найдена
     */
    @Transactional
    public void deleteDetailPage(Long id) {
        log.debug("Deleting detail page with id: {}", id);

        if (!detailPageRepository.existsById(id)) {
            throw new DetailPageNotFoundException(id);
        }

        detailPageRepository.deleteById(id);
        log.info("Detail page deleted with id: {}", id);
    }
}




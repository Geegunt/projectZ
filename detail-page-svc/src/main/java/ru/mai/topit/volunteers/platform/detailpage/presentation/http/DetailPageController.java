package ru.mai.topit.volunteers.platform.detailpage.presentation.http;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mai.topit.volunteers.platform.detailpage.application.DetailPageService;
import ru.mai.topit.volunteers.platform.detailpage.presentation.http.dto.DetailPageDtos;

/**
 * REST контроллер для работы с детальными страницами.
 * Предоставляет HTTP endpoints для CRUD операций и получения списка страниц.
 */
@RestController
@RequestMapping("/api/v1/detail-pages")
@RequiredArgsConstructor
@Tag(name = "Detail Pages", description = "API для управления детальными страницами")
public class DetailPageController {

    private final DetailPageService detailPageService;

    /**
     * Получает детальную страницу по ID.
     * При каждом запросе увеличивает счетчик просмотров.
     *
     * @param id идентификатор страницы
     * @return детальная страница
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить детальную страницу по ID", 
               description = "Возвращает детальную информацию о странице и увеличивает счетчик просмотров")
    public ResponseEntity<DetailPageDtos.DetailPageResponse> getDetailPageById(
            @Parameter(description = "ID детальной страницы", required = true)
            @PathVariable Long id) {
        var response = detailPageService.getDetailPageById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Получает список всех детальных страниц с пагинацией.
     * Поддерживает фильтрацию по типу страницы и статусу.
     *
     * @param pageType тип страницы (опционально)
     * @param status статус страницы (опционально)
     * @param pageable параметры пагинации (размер страницы, номер страницы, сортировка)
     * @return страница с результатами
     */
    @GetMapping
    @Operation(summary = "Получить список детальных страниц",
               description = "Возвращает список всех детальных страниц с поддержкой пагинации и фильтрации")
    public ResponseEntity<Page<DetailPageDtos.DetailPageResponse>> getAllDetailPages(
            @Parameter(description = "Тип страницы для фильтрации")
            @RequestParam(required = false) String pageType,
            
            @Parameter(description = "Статус страницы для фильтрации")
            @RequestParam(required = false) String status,
            
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        var response = detailPageService.getAllDetailPages(pageType, status, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Создает новую детальную страницу.
     *
     * @param request данные для создания страницы
     * @param authorId ID автора (опционально, может быть передано через заголовок)
     * @return созданная страница
     */
    @PostMapping
    @Operation(summary = "Создать новую детальную страницу",
               description = "Создает новую детальную страницу с указанными данными")
    public ResponseEntity<DetailPageDtos.DetailPageResponse> createDetailPage(
            @Valid @RequestBody DetailPageDtos.DetailPageCreateRequest request,
            
            @Parameter(description = "ID автора страницы")
            @RequestHeader(value = "X-Author-Id", required = false) Long authorId) {
        var response = detailPageService.createDetailPage(request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Обновляет существующую детальную страницу.
     *
     * @param id идентификатор страницы
     * @param request данные для обновления
     * @return обновленная страница
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновить детальную страницу",
               description = "Обновляет существующую детальную страницу")
    public ResponseEntity<DetailPageDtos.DetailPageResponse> updateDetailPage(
            @Parameter(description = "ID детальной страницы", required = true)
            @PathVariable Long id,
            
            @Valid @RequestBody DetailPageDtos.DetailPageUpdateRequest request) {
        var response = detailPageService.updateDetailPage(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Удаляет детальную страницу.
     *
     * @param id идентификатор страницы
     * @return статус 204 No Content при успешном удалении
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить детальную страницу",
               description = "Удаляет детальную страницу по ID")
    public ResponseEntity<Void> deleteDetailPage(
            @Parameter(description = "ID детальной страницы", required = true)
            @PathVariable Long id) {
        detailPageService.deleteDetailPage(id);
        return ResponseEntity.noContent().build();
    }
}




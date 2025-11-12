package ru.mai.topit.volunteers.platform.eventservice.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mai.topit.volunteers.platform.eventservice.domain.DetailPage;
import ru.mai.topit.volunteers.platform.eventservice.presentation.http.dto.DetailPageDtos;

/**
 * MapStruct маппер для преобразования между сущностью DetailPage и DTO.
 * MapStruct автоматически генерирует реализацию этого интерфейса во время компиляции.
 */
@Mapper(componentModel = "spring")
public interface DetailPageMapper {

    /**
     * Преобразует сущность DetailPage в DTO для ответа.
     * Игнорирует поля, которые не должны быть в ответе (например, внутренние ID).
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "pageType", source = "pageType")
    @Mapping(target = "metadata", source = "metadata")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "viewsCount", source = "viewsCount")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "publishedAt", source = "publishedAt")
    DetailPageDtos.DetailPageResponse toDto(DetailPage entity);

    /**
     * Обновляет существующую сущность данными из запроса на обновление.
     * Игнорирует поля, которые не должны обновляться напрямую (id, timestamps, authorId).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "viewsCount", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    void updateEntityFromRequest(DetailPageDtos.DetailPageUpdateRequest request, @MappingTarget DetailPage entity);

    /**
     * Преобразует запрос на создание в новую сущность.
     * authorId устанавливается отдельно в сервисе.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "viewsCount", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "status", defaultValue = "draft")
    DetailPage toEntity(DetailPageDtos.DetailPageCreateRequest request);
}




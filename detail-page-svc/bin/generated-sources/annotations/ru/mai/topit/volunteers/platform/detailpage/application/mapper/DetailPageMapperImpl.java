package ru.mai.topit.volunteers.platform.detailpage.application.mapper;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.mai.topit.volunteers.platform.detailpage.domain.DetailPage;
import ru.mai.topit.volunteers.platform.detailpage.presentation.http.dto.DetailPageDtos;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-11T02:23:02+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class DetailPageMapperImpl implements DetailPageMapper {

    @Override
    public DetailPageDtos.DetailPageResponse toDto(DetailPage entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String title = null;
        String description = null;
        String content = null;
        String pageType = null;
        Map<String, Object> metadata = null;
        String imageUrl = null;
        String status = null;
        Long authorId = null;
        Long viewsCount = null;
        OffsetDateTime createdAt = null;
        OffsetDateTime updatedAt = null;
        OffsetDateTime publishedAt = null;

        id = entity.getId();
        title = entity.getTitle();
        description = entity.getDescription();
        content = entity.getContent();
        pageType = entity.getPageType();
        Map<String, Object> map = entity.getMetadata();
        if ( map != null ) {
            metadata = new LinkedHashMap<String, Object>( map );
        }
        imageUrl = entity.getImageUrl();
        status = entity.getStatus();
        authorId = entity.getAuthorId();
        viewsCount = entity.getViewsCount();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();
        publishedAt = entity.getPublishedAt();

        DetailPageDtos.DetailPageResponse detailPageResponse = new DetailPageDtos.DetailPageResponse( id, title, description, content, pageType, metadata, imageUrl, status, authorId, viewsCount, createdAt, updatedAt, publishedAt );

        return detailPageResponse;
    }

    @Override
    public void updateEntityFromRequest(DetailPageDtos.DetailPageUpdateRequest request, DetailPage entity) {
        if ( request == null ) {
            return;
        }

        entity.setContent( request.content() );
        entity.setDescription( request.description() );
        entity.setImageUrl( request.imageUrl() );
        if ( entity.getMetadata() != null ) {
            Map<String, Object> map = request.metadata();
            if ( map != null ) {
                entity.getMetadata().clear();
                entity.getMetadata().putAll( map );
            }
            else {
                entity.setMetadata( null );
            }
        }
        else {
            Map<String, Object> map = request.metadata();
            if ( map != null ) {
                entity.setMetadata( new LinkedHashMap<String, Object>( map ) );
            }
        }
        entity.setPageType( request.pageType() );
        entity.setStatus( request.status() );
        entity.setTitle( request.title() );
    }

    @Override
    public DetailPage toEntity(DetailPageDtos.DetailPageCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        DetailPage detailPage = new DetailPage();

        if ( request.status() != null ) {
            detailPage.setStatus( request.status() );
        }
        else {
            detailPage.setStatus( "draft" );
        }
        detailPage.setContent( request.content() );
        detailPage.setDescription( request.description() );
        detailPage.setImageUrl( request.imageUrl() );
        Map<String, Object> map = request.metadata();
        if ( map != null ) {
            detailPage.setMetadata( new LinkedHashMap<String, Object>( map ) );
        }
        detailPage.setPageType( request.pageType() );
        detailPage.setTitle( request.title() );

        return detailPage;
    }
}

package ru.mai.topit.volunteers.platform.detailpage.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Сущность, представляющая детальную страницу с информацией.
 * Может использоваться для различных типов контента: события, проекты, статьи и т.д.
 */
@Getter
@Setter
@Entity
@Table(name = "detail_pages", schema = "detail_page")
@Data
public class DetailPage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Заголовок детальной страницы
     */
    @NotNull
    @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
    private String title;

    /**
     * Краткое описание (аннотация)
     */
    @Column(name = "description", length = Integer.MAX_VALUE, columnDefinition = "TEXT")
    private String description;

    /**
     * Полное содержимое страницы (HTML или Markdown)
     */
    @Column(name = "content", length = Integer.MAX_VALUE, columnDefinition = "TEXT")
    private String content;

    /**
     * Тип страницы (например: "event", "project", "article")
     */
    @NotNull
    @Column(name = "page_type", nullable = false, length = 50)
    private String pageType;

    /**
     * Дополнительные метаданные в формате JSON
     * Может содержать: даты, локации, участников, теги и т.д.
     */
    @ColumnDefault("'{}'")
    @Column(name = "metadata", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;

    /**
     * URL изображения или обложки
     */
    @Column(name = "image_url", length = Integer.MAX_VALUE)
    private String imageUrl;

    /**
     * Статус страницы (например: "draft", "published", "archived")
     */
    @NotNull
    @ColumnDefault("'draft'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * ID автора/создателя страницы
     */
    @Column(name = "author_id")
    private Long authorId;

    /**
     * Количество просмотров
     */
    @ColumnDefault("0")
    @Column(name = "views_count", nullable = false)
    private Long viewsCount;

    /**
     * Дата и время создания
     */
    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Дата и время последнего обновления
     */
    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Дата публикации (может быть null, если страница не опубликована)
     */
    @Column(name = "published_at")
    private OffsetDateTime publishedAt;
}




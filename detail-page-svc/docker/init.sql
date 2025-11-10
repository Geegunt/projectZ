-- Создание схемы
CREATE SCHEMA IF NOT EXISTS detail_page;

-- Создание таблицы detail_pages
CREATE TABLE IF NOT EXISTS detail_page.detail_pages (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR NOT NULL,
    description TEXT,
    content TEXT,
    page_type VARCHAR(50) NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}',
    image_url VARCHAR,
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    author_id BIGINT,
    views_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    published_at TIMESTAMP WITH TIME ZONE
);

-- Создание индексов для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_detail_pages_page_type ON detail_page.detail_pages(page_type);
CREATE INDEX IF NOT EXISTS idx_detail_pages_status ON detail_page.detail_pages(status);
CREATE INDEX IF NOT EXISTS idx_detail_pages_author_id ON detail_page.detail_pages(author_id);
CREATE INDEX IF NOT EXISTS idx_detail_pages_created_at ON detail_page.detail_pages(created_at DESC);

-- Комментарии к таблице и столбцам
COMMENT ON TABLE detail_page.detail_pages IS 'Таблица для хранения детальных страниц';
COMMENT ON COLUMN detail_page.detail_pages.id IS 'Уникальный идентификатор страницы';
COMMENT ON COLUMN detail_page.detail_pages.title IS 'Заголовок страницы';
COMMENT ON COLUMN detail_page.detail_pages.description IS 'Краткое описание страницы';
COMMENT ON COLUMN detail_page.detail_pages.content IS 'Полное содержимое страницы (HTML/Markdown)';
COMMENT ON COLUMN detail_page.detail_pages.page_type IS 'Тип страницы (event, project, article и т.д.)';
COMMENT ON COLUMN detail_page.detail_pages.metadata IS 'Дополнительные метаданные в формате JSON';
COMMENT ON COLUMN detail_page.detail_pages.image_url IS 'URL изображения или обложки';
COMMENT ON COLUMN detail_page.detail_pages.status IS 'Статус страницы (draft, published, archived)';
COMMENT ON COLUMN detail_page.detail_pages.author_id IS 'ID автора/создателя страницы';
COMMENT ON COLUMN detail_page.detail_pages.views_count IS 'Количество просмотров страницы';
COMMENT ON COLUMN detail_page.detail_pages.created_at IS 'Дата и время создания';
COMMENT ON COLUMN detail_page.detail_pages.updated_at IS 'Дата и время последнего обновления';
COMMENT ON COLUMN detail_page.detail_pages.published_at IS 'Дата и время публикации';


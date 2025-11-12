-- Создание таблицы событий
CREATE TABLE IF NOT EXISTS event_service.events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    content TEXT,
    category_id BIGINT NOT NULL REFERENCES event_service.categories(id) ON DELETE RESTRICT,
    image_url VARCHAR,
    status VARCHAR(20) NOT NULL DEFAULT 'draft' CHECK (status IN ('draft', 'published', 'cancelled', 'completed')),
    event_type VARCHAR(50) NOT NULL DEFAULT 'online' CHECK (event_type IN ('online', 'offline', 'hybrid')),
    max_participants INTEGER,
    current_participants INTEGER NOT NULL DEFAULT 0,
    age_restriction INTEGER CHECK (age_restriction >= 0 AND age_restriction <= 18),
    location_name VARCHAR(200),
    location_address TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    online_url VARCHAR,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    registration_deadline TIMESTAMP WITH TIME ZONE,
    author_id BIGINT NOT NULL,
    views_count BIGINT NOT NULL DEFAULT 0,
    is_featured BOOLEAN NOT NULL DEFAULT false,
    tags TEXT[], -- Массив тегов
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    published_at TIMESTAMP WITH TIME ZONE
);

-- Создание индексов
CREATE INDEX IF NOT EXISTS idx_events_category_id ON event_service.events(category_id);
CREATE INDEX IF NOT EXISTS idx_events_status ON event_service.events(status);
CREATE INDEX IF NOT EXISTS idx_events_event_type ON event_service.events(event_type);
CREATE INDEX IF NOT EXISTS idx_events_start_date ON event_service.events(start_date);
CREATE INDEX IF NOT EXISTS idx_events_author_id ON event_service.events(author_id);
CREATE INDEX IF NOT EXISTS idx_events_is_featured ON event_service.events(is_featured);
CREATE INDEX IF NOT EXISTS idx_events_tags ON event_service.events USING GIN(tags);
CREATE INDEX IF NOT EXISTS idx_events_location ON event_service.events(latitude, longitude) WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- Комментарии к таблице и столбцам
COMMENT ON TABLE event_service.events IS 'Основная таблица событий';
COMMENT ON COLUMN event_service.events.id IS 'Уникальный идентификатор события';
COMMENT ON COLUMN event_service.events.title IS 'Заголовок события';
COMMENT ON COLUMN event_service.events.description IS 'Краткое описание события';
COMMENT ON COLUMN event_service.events.content IS 'Полное содержимое события (HTML/Markdown)';
COMMENT ON COLUMN event_service.events.category_id IS 'ID категории события';
COMMENT ON COLUMN event_service.events.image_url IS 'URL изображения или обложки';
COMMENT ON COLUMN event_service.events.status IS 'Статус события (draft, published, cancelled, completed)';
COMMENT ON COLUMN event_service.events.event_type IS 'Тип мероприятия (online, offline, hybrid)';
COMMENT ON COLUMN event_service.events.max_participants IS 'Максимальное количество участников';
COMMENT ON COLUMN event_service.events.current_participants IS 'Текущее количество участников';
COMMENT ON COLUMN event_service.events.age_restriction IS 'Возрастное ограничение (0+ до 18+)';
COMMENT ON COLUMN event_service.events.location_name IS 'Название места проведения';
COMMENT ON COLUMN event_service.events.location_address IS 'Адрес места проведения';
COMMENT ON COLUMN event_service.events.latitude IS 'Широта места проведения';
COMMENT ON COLUMN event_service.events.longitude IS 'Долгота места проведения';
COMMENT ON COLUMN event_service.events.online_url IS 'URL для онлайн участия';
COMMENT ON COLUMN event_service.events.start_date IS 'Дата и время начала события';
COMMENT ON COLUMN event_service.events.end_date IS 'Дата и время окончания события';
COMMENT ON COLUMN event_service.events.registration_deadline IS 'Дедлайн регистрации на событие';
COMMENT ON COLUMN event_service.events.author_id IS 'ID автора/организатора события';
COMMENT ON COLUMN event_service.events.views_count IS 'Количество просмотров события';
COMMENT ON COLUMN event_service.events.is_featured IS 'Является ли событие рекомендуемым';
COMMENT ON COLUMN event_service.events.tags IS 'Теги события';
COMMENT ON COLUMN event_service.events.created_at IS 'Дата и время создания';
COMMENT ON COLUMN event_service.events.updated_at IS 'Дата и время последнего обновления';
COMMENT ON COLUMN event_service.events.published_at IS 'Дата и время публикации';

-- Триггер для обновления updated_at
CREATE OR REPLACE FUNCTION event_service.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_events_updated_at 
    BEFORE UPDATE ON event_service.events 
    FOR EACH ROW 
    EXECUTE FUNCTION event_service.update_updated_at_column();
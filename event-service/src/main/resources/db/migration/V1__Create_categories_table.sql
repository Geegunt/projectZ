-- Создание таблицы категорий событий
CREATE TABLE IF NOT EXISTS event_service.categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    color VARCHAR(7), -- HEX цвет в формате #RRGGBB
    icon VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Создание индексов
CREATE INDEX IF NOT EXISTS idx_categories_is_active ON event_service.categories(is_active);
CREATE INDEX IF NOT EXISTS idx_categories_sort_order ON event_service.categories(sort_order);

-- Комментарии к таблице и столбцам
COMMENT ON TABLE event_service.categories IS 'Категории для классификации событий';
COMMENT ON COLUMN event_service.categories.id IS 'Уникальный идентификатор категории';
COMMENT ON COLUMN event_service.categories.name IS 'Название категории';
COMMENT ON COLUMN event_service.categories.description IS 'Описание категории';
COMMENT ON COLUMN event_service.categories.color IS 'Цвет категории в HEX формате';
COMMENT ON COLUMN event_service.categories.icon IS 'Иконка категории';
COMMENT ON COLUMN event_service.categories.is_active IS 'Активна ли категория';
COMMENT ON COLUMN event_service.categories.sort_order IS 'Порядок сортировки';
COMMENT ON COLUMN event_service.categories.created_at IS 'Дата и время создания';
COMMENT ON COLUMN event_service.categories.updated_at IS 'Дата и время последнего обновления';

-- Вставка базовых категорий
INSERT INTO event_service.categories (name, description, color, icon, sort_order) VALUES
('Концерты', 'Музыкальные мероприятия и выступления', '#FF6B6B', 'music', 1),
('Спорт', 'Спортивные события и соревнования', '#4ECDC4', 'sport', 2),
('Образование', 'Лекции, семинары и образовательные курсы', '#45B7D1', 'education', 3),
('Бизнес', 'Деловые мероприятия, конференции и нетворкинг', '#96CEB4', 'business', 4),
('Развлечения', 'Развлекательные мероприятия и фестивали', '#FFEAA7', 'entertainment', 5),
('Волонтерство', 'Благотворительные и социальные акции', '#DDA0DD', 'volunteer', 6),
('Искусство', 'Выставки, театральные постановки и творческие мероприятия', '#98D8C8', 'art', 7);
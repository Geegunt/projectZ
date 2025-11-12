-- Создание схемы
CREATE SCHEMA IF NOT EXISTS event_service;

-- Установка прав доступа
GRANT ALL ON SCHEMA event_service TO eventservice;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA event_service TO eventservice;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA event_service TO eventservice;

-- Комментарий к схеме
COMMENT ON SCHEMA event_service IS 'Схема для сервиса управления событиями';


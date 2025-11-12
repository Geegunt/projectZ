-- Таблица для категорий мероприятий (экология, социальное и т.д.)
CREATE TABLE event_categories (
                                  id BIGSERIAL PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL UNIQUE
);

-- Основная таблица с мероприятиями
CREATE TABLE events (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        start_time TIMESTAMP NOT NULL,
                        duration_hours INT NOT NULL,  -- Длительность в часах [cite: 156]
                        location VARCHAR(255),
                        participant_limit INT NOT NULL, -- Кол-во участников [cite: 156]
                        chat_link VARCHAR(255),       -- Ссылка на чат [cite: 156]
                        image_url VARCHAR(255),
    -- Связь с категорией (можно сделать и Many-to-Many, но для простоты начнем с Many-to-One)
                        category_id BIGINT REFERENCES event_categories(id),
    -- ID организатора из User Service (предполагаем, что он хранится как UUID или LONG)
                        organizer_id BIGINT NOT NULL,
                        status VARCHAR(50) NOT NULL DEFAULT 'PLANNED' -- (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)
);

-- Таблица для заявок на участие
CREATE TABLE event_applications (
                                    id BIGSERIAL PRIMARY KEY,
    -- ID волонтера из User Service
                                    user_id BIGINT NOT NULL,
                                    event_id BIGINT NOT NULL REFERENCES events(id),
                                    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- (PENDING, APPROVED, REJECTED)
                                    attended BOOLEAN DEFAULT FALSE, -- Подтверждение участия
                                    application_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Уникальная связка, чтобы пользователь не мог подать 2 заявки на 1 ивент
                                    UNIQUE(user_id, event_id)
);
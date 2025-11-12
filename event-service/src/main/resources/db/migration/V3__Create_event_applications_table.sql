-- Создание таблицы заявок на участие в событиях
CREATE TABLE IF NOT EXISTS event_service.event_applications (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES event_service.events(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected', 'cancelled')),
    application_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    contact_info JSONB, -- Контактная информация заявителя
    message TEXT, -- Сообщение от заявителя
    reviewed_by BIGINT, -- ID пользователя, который рассмотрел заявку
    review_date TIMESTAMP WITH TIME ZONE, -- Дата рассмотрения заявки
    review_comment TEXT, -- Комментарий при рассмотрении
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Создание индексов
CREATE INDEX IF NOT EXISTS idx_event_applications_event_id ON event_service.event_applications(event_id);
CREATE INDEX IF NOT EXISTS idx_event_applications_user_id ON event_service.event_applications(user_id);
CREATE INDEX IF NOT EXISTS idx_event_applications_status ON event_service.event_applications(status);
CREATE INDEX IF NOT EXISTS idx_event_applications_application_date ON event_service.event_applications(application_date);
CREATE INDEX IF NOT EXISTS idx_event_applications_reviewed_by ON event_service.event_applications(reviewed_by);

-- Уникальный индекс для предотвращения дублирования заявок
CREATE UNIQUE INDEX IF NOT EXISTS idx_event_applications_unique_event_user 
    ON event_service.event_applications(event_id, user_id) 
    WHERE status NOT IN ('cancelled', 'rejected');

-- Комментарии к таблице и столбцам
COMMENT ON TABLE event_service.event_applications IS 'Заявки на участие в событиях';
COMMENT ON COLUMN event_service.event_applications.id IS 'Уникальный идентификатор заявки';
COMMENT ON COLUMN event_service.event_applications.event_id IS 'ID события';
COMMENT ON COLUMN event_service.event_applications.user_id IS 'ID пользователя, подавшего заявку';
COMMENT ON COLUMN event_service.event_applications.status IS 'Статус заявки (pending, approved, rejected, cancelled)';
COMMENT ON COLUMN event_service.event_applications.application_date IS 'Дата подачи заявки';
COMMENT ON COLUMN event_service.event_applications.contact_info IS 'Контактная информация заявителя в JSON';
COMMENT ON COLUMN event_service.event_applications.message IS 'Сообщение от заявителя';
COMMENT ON COLUMN event_service.event_applications.reviewed_by IS 'ID пользователя, рассмотревшего заявку';
COMMENT ON COLUMN event_service.event_applications.review_date IS 'Дата рассмотрения заявки';
COMMENT ON COLUMN event_service.event_applications.review_comment IS 'Комментарий при рассмотрении';
COMMENT ON COLUMN event_service.event_applications.created_at IS 'Дата и время создания';
COMMENT ON COLUMN event_service.event_applications.updated_at IS 'Дата и время последнего обновления';

-- Триггер для обновления updated_at
CREATE TRIGGER update_event_applications_updated_at 
    BEFORE UPDATE ON event_service.event_applications 
    FOR EACH ROW 
    EXECUTE FUNCTION event_service.update_updated_at_column();

-- Триггер для обновления счетчика участников при изменении статуса заявки
CREATE OR REPLACE FUNCTION event_service.update_event_participants_count()
RETURNS TRIGGER AS $$
BEGIN
    -- Обновляем счетчик участников для события
    UPDATE event_service.events 
    SET current_participants = (
        SELECT COUNT(*) 
        FROM event_service.event_applications 
        WHERE event_id = NEW.event_id AND status = 'approved'
    )
    WHERE id = NEW.event_id;
    
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Триггеры для обновления счетчика участников
CREATE TRIGGER update_participants_on_insert
    AFTER INSERT ON event_service.event_applications
    FOR EACH ROW
    WHEN (NEW.status = 'approved')
    EXECUTE FUNCTION event_service.update_event_participants_count();

CREATE TRIGGER update_participants_on_update
    AFTER UPDATE ON event_service.event_applications
    FOR EACH ROW
    WHEN (OLD.status IS DISTINCT FROM NEW.status)
    EXECUTE FUNCTION event_service.update_event_participants_count();

CREATE TRIGGER update_participants_on_delete
    AFTER DELETE ON event_service.event_applications
    FOR EACH ROW
    WHEN (OLD.status = 'approved')
    EXECUTE FUNCTION event_service.update_event_participants_count();
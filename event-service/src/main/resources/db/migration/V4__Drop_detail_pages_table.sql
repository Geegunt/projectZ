-- Удаление старой таблицы detail_pages, так как она заменена новой структурой
DROP TABLE IF EXISTS event_service.detail_pages CASCADE;

-- Удаление триггеров и функций, если они существуют
DROP TRIGGER IF EXISTS update_detail_pages_updated_at ON event_service.detail_pages;
DROP FUNCTION IF EXISTS event_service.update_detail_pages_updated_at();
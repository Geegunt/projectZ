# Event Service

Микросервис для управления событиями. Предоставляет REST API для создания, чтения, обновления и удаления событий.

## Быстрый старт

### 1. Запуск PostgreSQL
```bash
cd event-service
docker compose -f docker/docker-compose.yml up -d
```

### 2. Запуск приложения
```bash
./gradlew bootRun
```

### 3. Открытие Swagger UI
```
http://localhost:8081/swagger-ui
```

### 4. Остановка сервисов
```bash
# Остановка приложения: Ctrl+C

# Остановка PostgreSQL
docker compose -f docker/docker-compose.yml down
```

## Архитектура

Проект следует принципам Clean Architecture с разделением на слои:
- **Domain Layer** - бизнес-сущности
- **Application Layer** - бизнес-логика и сервисы
- **Infrastructure Layer** - работа с БД и конфигурация
- **Presentation Layer** - REST контроллеры и DTO

## Основные компоненты

- **Event** - основная сущность события
- **Category** - категория события
- **EventApplication** - заявка на участие в событии
- **PostgreSQL** - хранилище данных
- **OpenAPI/Swagger** - документация API

## Конфигурация

Приложение настраивается через `src/main/resources/application.yml`:
- `spring.application.name` - имя сервиса
- `spring.datasource` - параметры подключения к БД
- `spring.jpa.properties.hibernate.default_schema` - схема БД
- `server.port` - порт приложения (по умолчанию 8081)

## API Endpoints

- `GET /api/v1/events` - получить список событий
- `GET /api/v1/events/{id}` - получить событие по ID
- `POST /api/v1/events` - создать новое событие
- `PUT /api/v1/events/{id}` - обновить событие
- `DELETE /api/v1/events/{id}` - удалить событие


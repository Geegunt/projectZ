# Detail Page Service

Микросервис для управления детальными страницами с информацией. Предоставляет REST API для создания, чтения, обновления и удаления детальных страниц, которые могут использоваться для различных типов контента (события, проекты, статьи и т.д.).

## Архитектура проекта

Проект следует принципам **Clean Architecture** и разделен на несколько слоев:

### 1. Domain Layer (Доменный слой)
**Путь:** `src/main/java/.../domain/`

Содержит бизнес-сущности и доменную логику.

#### `DetailPage.java`
- **Назначение:** JPA-сущность, представляющая детальную страницу в базе данных
- **Основные поля:**
  - `id` - уникальный идентификатор
  - `title` - заголовок страницы
  - `description` - краткое описание
  - `content` - полное содержимое (HTML/Markdown)
  - `pageType` - тип страницы (event, project, article и т.д.)
  - `metadata` - JSON-поле для дополнительных данных
  - `imageUrl` - URL изображения/обложки
  - `status` - статус (draft, published, archived)
  - `authorId` - ID автора
  - `viewsCount` - счетчик просмотров
  - `createdAt`, `updatedAt`, `publishedAt` - временные метки

- **Особенности:**
  - Использует аннотации JPA для маппинга на таблицу `detail_pages` в схеме `detail_page`
  - Поле `metadata` хранится как JSON в PostgreSQL
  - Автоматическое управление временными метками через `@ColumnDefault`

### 2. Application Layer (Слой приложения)
**Путь:** `src/main/java/.../application/`

Содержит бизнес-логику и сервисы.

#### `DetailPageService.java`
- **Назначение:** Основной сервис для работы с детальными страницами
- **Методы:**
  - `getDetailPageById(Long id)` - получение страницы по ID с увеличением счетчика просмотров
  - `getAllDetailPages(...)` - получение списка с пагинацией и фильтрацией
  - `createDetailPage(...)` - создание новой страницы
  - `updateDetailPage(...)` - обновление существующей страницы
  - `deleteDetailPage(Long id)` - удаление страницы

- **Особенности:**
  - Использует `@Transactional` для управления транзакциями
  - Автоматически увеличивает счетчик просмотров при чтении
  - Поддерживает фильтрацию по типу и статусу
  - Автоматически устанавливает `publishedAt` при публикации

#### `DetailPageMapper.java`
- **Назначение:** MapStruct интерфейс для преобразования между сущностями и DTO
- **Методы:**
  - `toDto(DetailPage)` - преобразование сущности в DTO ответа
  - `updateEntityFromRequest(...)` - обновление сущности из DTO запроса
  - `toEntity(...)` - создание сущности из DTO запроса

- **Особенности:**
  - MapStruct генерирует реализацию во время компиляции
  - Автоматически игнорирует поля, которые не должны обновляться (id, timestamps)
  - Использует `componentModel = "spring"` для интеграции с Spring

#### `exception/DetailPageNotFoundException.java`
- **Назначение:** Кастомное исключение для случаев, когда страница не найдена
- Используется для возврата понятных ошибок клиенту

### 3. Infrastructure Layer (Инфраструктурный слой)
**Путь:** `src/main/java/.../infrastructure/`

Содержит технические детали реализации (база данных, внешние сервисы).

#### `repository/DetailPageRepository.java`
- **Назначение:** Spring Data JPA репозиторий для работы с базой данных
- **Методы:**
  - `findById(Long)` - поиск по ID
  - `findByPageType(...)` - поиск по типу с пагинацией
  - `findByStatus(...)` - поиск по статусу с пагинацией
  - `findByPageTypeAndStatus(...)` - комбинированный поиск
  - `existsById(Long)` - проверка существования

- **Особенности:**
  - Spring Data JPA автоматически генерирует SQL-запросы на основе имен методов
  - Поддержка пагинации через `Pageable`
  - Не требует написания SQL вручную

#### `config/OpenApiConfig.java`
- **Назначение:** Конфигурация Swagger/OpenAPI для документации API
- Настраивает метаданные API (название, описание, версия)
- Делает API доступным через Swagger UI по адресу `/swagger-ui`

### 4. Presentation Layer (Слой представления)
**Путь:** `src/main/java/.../presentation/`

Содержит HTTP контроллеры и DTO для взаимодействия с клиентами.

#### `http/DetailPageController.java`
- **Назначение:** REST контроллер, предоставляющий HTTP endpoints
- **Endpoints:**
  - `GET /api/v1/detail-pages/{id}` - получить страницу по ID
  - `GET /api/v1/detail-pages` - получить список страниц (с фильтрацией и пагинацией)
  - `POST /api/v1/detail-pages` - создать новую страницу
  - `PUT /api/v1/detail-pages/{id}` - обновить страницу
  - `DELETE /api/v1/detail-pages/{id}` - удалить страницу

- **Особенности:**
  - Использует `@RestController` для автоматической сериализации JSON
  - Валидация входных данных через `@Valid`
  - Swagger аннотации для документации API
  - Поддержка пагинации через `Pageable`
  - Опциональный заголовок `X-Author-Id` для указания автора

#### `http/dto/DetailPageDtos.java`
- **Назначение:** Data Transfer Objects для передачи данных
- **Классы:**
  - `DetailPageResponse` - DTO для ответа (содержит все поля)
  - `DetailPageCreateRequest` - DTO для создания (с валидацией обязательных полей)
  - `DetailPageUpdateRequest` - DTO для обновления (все поля опциональны)

- **Особенности:**
  - Использует Java Records для неизменяемых DTO
  - Jakarta Validation аннотации для валидации (`@NotBlank`, `@Size`)
  - Разделение на Request/Response для безопасности и гибкости

#### `http/GlobalExceptionHandler.java`
- **Назначение:** Глобальный обработчик исключений
- **Обрабатывает:**
  - `DetailPageNotFoundException` - 404 Not Found
  - `MethodArgumentNotValidException` - ошибки валидации (400 Bad Request)
  - `ConstraintViolationException` - нарушения ограничений (400 Bad Request)
  - `IllegalArgumentException` - неверные аргументы (400 Bad Request)
  - Все остальные исключения - 500 Internal Server Error

- **Особенности:**
  - Возвращает структурированные ответы об ошибках
  - Логирует все ошибки для отладки
  - Генерирует уникальный `errorId` для каждой ошибки
  - Включает timestamp и путь запроса

## Конфигурация

### `application.yml`
- **База данных:** PostgreSQL на порту 25433
- **Порт приложения:** 8081
- **JPA:** Режим `validate` (проверяет схему, не создает таблицы)
- **Swagger:** Доступен по `/swagger-ui`

### `build.gradle`
- **Java:** Версия 21
- **Spring Boot:** 3.5.5
- **Основные зависимости:**
  - Spring Data JPA (работа с БД)
  - Spring Web (REST API)
  - MapStruct (маппинг объектов)
  - Lombok (уменьшение boilerplate кода)
  - SpringDoc OpenAPI (Swagger документация)
  - PostgreSQL Driver

## Поток данных

1. **HTTP запрос** → `DetailPageController`
2. **Валидация** → Jakarta Validation проверяет DTO
3. **Сервисный слой** → `DetailPageService` выполняет бизнес-логику
4. **Маппинг** → `DetailPageMapper` преобразует DTO ↔ Entity
5. **Репозиторий** → `DetailPageRepository` работает с БД
6. **Ответ** → DTO сериализуется в JSON и возвращается клиенту

## Примеры использования

### Создание страницы
```bash
POST /api/v1/detail-pages
Content-Type: application/json
X-Author-Id: 123

{
  "title": "Волонтерское событие",
  "description": "Описание события",
  "content": "Полное описание...",
  "pageType": "event",
  "status": "draft",
  "metadata": {
    "date": "2024-12-25",
    "location": "Москва"
  }
}
```

### Получение страницы
```bash
GET /api/v1/detail-pages/1
```

### Получение списка с фильтрацией
```bash
GET /api/v1/detail-pages?pageType=event&status=published&page=0&size=10
```

### Обновление страницы
```bash
PUT /api/v1/detail-pages/1
Content-Type: application/json

{
  "title": "Обновленный заголовок",
  "status": "published"
}
```

## Особенности реализации

1. **Clean Architecture:** Четкое разделение слоев обеспечивает тестируемость и поддерживаемость
2. **Транзакционность:** Все операции с БД выполняются в транзакциях
3. **Валидация:** Входные данные валидируются на уровне контроллера
4. **Обработка ошибок:** Централизованная обработка исключений
5. **Документация:** Автоматическая генерация Swagger документации
6. **Пагинация:** Поддержка больших списков через Spring Data Pageable
7. **Фильтрация:** Гибкая фильтрация по типу и статусу
8. **Счетчик просмотров:** Автоматическое увеличение при каждом чтении

## Запуск приложения

### Быстрый старт

1. Запустите PostgreSQL:
   ```bash
   docker compose -f docker/docker-compose.yml up -d
   ```

2. Запустите приложение:
   ```bash
   ./gradlew bootRun
   ```

3. Откройте Swagger UI:
   ```
   http://localhost:8081/swagger-ui
   ```

### Подробная инструкция

Смотрите файлы:
- **`DETAIL_PAGE_EXPLANATION.md`** — подробное объяснение работы проекта и пошаговая инструкция по проверке
- **`QUICK_START.md`** — быстрая инструкция по запуску

## Схема базы данных

Таблица `detail_pages` в схеме `detail_page`:
- Все поля соответствуют полям сущности `DetailPage`
- Поле `metadata` имеет тип JSONB в PostgreSQL
- Индексы создаются автоматически при запуске Docker контейнера (см. `docker/init.sql`)

## Документация

- **`DETAIL_PAGE_EXPLANATION.md`** — подробное объяснение работы всех компонентов проекта, потоки данных, примеры использования API
- **`QUICK_START.md`** — быстрая инструкция по запуску и тестированию
- **`README.md`** — общее описание проекта (этот файл)




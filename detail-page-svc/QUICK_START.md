# Быстрый старт - detail-page-svc

## Быстрая инструкция по запуску

### 1. Запуск PostgreSQL
```bash
cd /Users/glebgrigorev/Desktop/programming/projectEpsilan/detail-page-svc
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

### 4. Тестирование API
```bash
# Создание страницы
curl -X POST http://localhost:8081/api/v1/detail-pages \
  -H "Content-Type: application/json" \
  -H "X-Author-Id: 123" \
  -d '{
    "title": "Тестовая страница",
    "pageType": "event",
    "status": "draft"
  }'

# Получение страницы
curl http://localhost:8081/api/v1/detail-pages/1

# Получение списка
curl "http://localhost:8081/api/v1/detail-pages?page=0&size=10"
```

## Подробная инструкция

Смотрите файл `DETAIL_PAGE_EXPLANATION.md` для подробного объяснения работы проекта и пошаговой инструкции по проверке.

## Остановка сервисов

```bash
# Остановка приложения: Ctrl+C

# Остановка PostgreSQL
docker compose -f docker/docker-compose.yml down
```


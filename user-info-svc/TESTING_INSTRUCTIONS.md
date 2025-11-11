# Пошаговая инструкция по проверке работы User Info Service

## Предварительные требования

1. **Java 21** - должен быть установлен и доступен в PATH
2. **Docker и Docker Compose** - для запуска инфраструктуры (PostgreSQL, Redis)
3. **Gradle** - встроен в проект (gradlew), но можно использовать системный
4. **curl** или **Postman** - для тестирования API
5. **Интернет-соединение** - для загрузки зависимостей при первой сборке

---

## Шаг 1: Проверка окружения

### 1.1. Проверка Java
```bash
java -version
```
Должна быть версия 21 или выше.

### 1.2. Проверка Docker
```bash
docker --version
docker compose version
```

### 1.3. Переход в директорию проекта
```bash
cd /Users/glebgrigorev/Desktop/programming/user-info-svc
```

---

## Шаг 2: Запуск инфраструктуры (PostgreSQL и Redis)

### 2.1. Запуск Docker контейнеров
```bash
cd docker
docker compose up -d
```

Эта команда запустит:
- **PostgreSQL** на порту `25432` (внутренний порт 5432)
- **Redis** на порту `6380` (внутренний порт 6379)
- **Liquibase миграции** - автоматически создаст схему БД

### 2.2. Проверка статуса контейнеров
```bash
docker compose ps
```

Должны быть запущены:
- `postgresDb` - статус `Up (healthy)`
- `redis_container` - статус `Up`
- `migrations` - статус `Exited (0)` (успешно выполнился)

### 2.3. Проверка подключения к PostgreSQL
```bash
docker exec -it postgresDb psql -U userinfo -d userinfo_db -c "\dt user_info.*"
```

Должна отобразиться таблица `users` в схеме `user_info`.

### 2.4. Проверка подключения к Redis
```bash
docker exec -it redis_container redis-cli -a redispassword ping
```

Должен вернуться ответ `PONG`.

---

## Шаг 3: Проверка наличия ProfileMapper

**ВАЖНО:** В проекте используется `ProfileMapper`, но файл отсутствует. Это может вызвать ошибку компиляции.

### 3.1. Проверка наличия файла
```bash
ls -la src/main/java/ru/mai/topit/volunteers/platform/userinfo/application/mapper/ProfileMapper.java
```

Если файл не существует, **MapStruct должен сгенерировать его при компиляции**, но интерфейс должен быть создан вручную. Если компиляция не пройдет, нужно будет создать этот файл (но по инструкции мы ничего не исправляем).

---

## Шаг 4: Сборка проекта

### 4.1. Очистка предыдущих сборок (опционально)
```bash
cd /Users/glebgrigorev/Desktop/programming/user-info-svc
./gradlew clean
```

### 4.2. Сборка проекта
```bash
./gradlew build
```

Эта команда:
- Скачает все зависимости
- Скомпилирует Java код
- Сгенерирует MapStruct мапперы (включая ProfileMapper, если интерфейс существует)
- Запустит тесты
- Создаст JAR файл

### 4.3. Если сборка не удалась
- Проверьте логи ошибок
- Убедитесь, что все зависимости доступны
- Проверьте, что порты 25432 и 6380 свободны

---

## Шаг 5: Запуск приложения

### 5.1. Запуск Spring Boot приложения
```bash
./gradlew bootRun
```

Или если хотите запустить из собранного JAR:
```bash
java -jar build/libs/user-info-0.0.1-SNAPSHOT.jar
```

### 5.2. Проверка запуска
Приложение должно запуститься на порту **8080** (по умолчанию Spring Boot).

В логах должны быть сообщения:
- `Started UserInfoApplication`
- Подключение к PostgreSQL
- Подключение к Redis

### 5.3. Проверка доступности
Откройте в браузере или через curl:
```bash
curl http://localhost:8080/swagger-ui/index.html
```

Должна открыться страница Swagger UI.

---

## Шаг 6: Тестирование API через Swagger UI

### 6.1. Открыть Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

### 6.2. Просмотр доступных эндпоинтов
В Swagger UI вы увидите:
- **Auth Controller** - эндпоинты `/auth/*`
- **Profile Controller** - эндпоинты `/profile/*`

---

## Шаг 7: Тестирование API через curl

### 7.1. Регистрация нового пользователя

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "login": "testuser",
    "password": "testpass123",
    "fullName": "Test User",
    "personalEmail": "test@example.com",
    "maiEmail": "test@mai.ru",
    "institute": "IT",
    "studentGroup": "M21-123",
    "birthDate": "2000-01-01",
    "clothingSize": "M",
    "social": {
      "telegram": "@testuser",
      "vk": "vk.com/testuser"
    },
    "contactEmail": "contact@example.com"
  }'
```

**Ожидаемый результат:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Сохраните токены** для следующих шагов!

### 7.2. Попытка регистрации с тем же логином (проверка ошибки)

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "login": "testuser",
    "password": "anotherpass"
  }'
```

**Ожидаемый результат:** HTTP 409 CONFLICT
```json
{
  "errorId": "...",
  "timestamp": "...",
  "status": 409,
  "code": "USER_EXISTS",
  "message": "User with login 'testuser' already exists",
  "path": "/auth/signup"
}
```

### 7.3. Вход (Login)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "testuser",
    "password": "testpass123"
  }'
```

**Ожидаемый результат:** Новая пара токенов (accessToken, refreshToken)

### 7.4. Вход с неверным паролем (проверка ошибки)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "testuser",
    "password": "wrongpassword"
  }'
```

**Ожидаемый результат:** HTTP 401 UNAUTHORIZED
```json
{
  "errorId": "...",
  "status": 401,
  "code": "INVALID_CREDENTIALS",
  "message": "Invalid credentials"
}
```

### 7.5. Получение профиля (требует аутентификации)

**Замените `YOUR_ACCESS_TOKEN` на реальный accessToken из шага 7.1 или 7.3:**

```bash
curl -X GET http://localhost:8080/profile/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Ожидаемый результат:**
```json
{
  "id": 1,
  "login": "testuser",
  "fullName": "Test User",
  "personalEmail": "test@example.com",
  "maiEmail": "test@mai.ru",
  "institute": "IT",
  "studentGroup": "M21-123",
  "birthDate": "2000-01-01",
  "clothingSize": "M",
  "social": {
    "telegram": "@testuser",
    "vk": "vk.com/testuser"
  },
  "contactEmail": "contact@example.com",
  "role": "VOLUNTEER"
}
```

### 7.6. Попытка доступа без токена (проверка безопасности)

```bash
curl -X GET http://localhost:8080/profile/me
```

**Ожидаемый результат:** HTTP 401 UNAUTHORIZED

### 7.7. Обновление профиля

**Замените `YOUR_ACCESS_TOKEN` на реальный токен:**

```bash
curl -X PUT http://localhost:8080/profile/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Updated Test User",
    "personalEmail": "updated@example.com",
    "institute": "Updated IT",
    "social": {
      "telegram": "@updateduser",
      "vk": "vk.com/updateduser"
    }
  }'
```

**Ожидаемый результат:** Обновленный профиль

### 7.8. Повторное получение профиля (проверка кэширования)

```bash
curl -X GET http://localhost:8080/profile/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

После обновления профиля кэш должен быть инвалидирован, и вы получите обновленные данные.

### 7.9. Обновление токенов (Refresh)

**Замените `YOUR_REFRESH_TOKEN` на реальный refreshToken:**

```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

**Ожидаемый результат:** Новая пара токенов (accessToken, refreshToken)

**Важно:** Старый refresh токен должен быть отозван и больше не работать.

### 7.10. Попытка использовать старый refresh токен (проверка отзыва)

```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "OLD_REFRESH_TOKEN_FROM_STEP_7.9"
  }'
```

**Ожидаемый результат:** HTTP 401 UNAUTHORIZED
```json
{
  "code": "INVALID_REFRESH_TOKEN",
  "message": "Invalid refresh token"
}
```

### 7.11. Выход (Logout)

**Используйте актуальный refreshToken:**

```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

**Ожидаемый результат:** HTTP 204 No Content

### 7.12. Попытка использовать refresh токен после выхода

```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "REFRESH_TOKEN_FROM_STEP_7.11"
  }'
```

**Ожидаемый результат:** HTTP 401 UNAUTHORIZED (токен отозван)

---

## Шаг 8: Проверка работы Redis (кэширование и токены)

### 8.1. Проверка кэша профиля в Redis

```bash
docker exec -it redis_container redis-cli -a redispassword
```

В Redis CLI выполните:
```redis
KEYS op:cache:*
```

Должны отобразиться ключи вида `op:cache:user:profile:testuser`

```redis
GET op:cache:user:profile:testuser
```

Должен вернуться JSON с профилем пользователя.

### 8.2. Проверка refresh токенов в Redis

```redis
KEYS auth:refresh:*
```

Должны отобразиться ключи вида `auth:refresh:1:TOKEN_VALUE`

```redis
TTL auth:refresh:1:TOKEN_VALUE
```

Должен вернуться TTL в секундах (около 2592000 для 30 дней).

### 8.3. Выход из Redis CLI
```redis
exit
```

---

## Шаг 9: Проверка базы данных

### 9.1. Проверка созданного пользователя

```bash
docker exec -it postgresDb psql -U userinfo -d userinfo_db
```

В PostgreSQL выполните:
```sql
SELECT id, login, full_name, personal_email, mai_email, institute, student_group, role, created_at 
FROM user_info.users;
```

Должен отобразиться созданный пользователь.

### 9.2. Проверка хеширования пароля

```sql
SELECT login, password FROM user_info.users WHERE login = 'testuser';
```

Пароль должен быть захеширован (начинаться с `$2a$` или `$2b$` - BCrypt).

### 9.3. Проверка JSON поля social

```sql
SELECT login, social FROM user_info.users WHERE login = 'testuser';
```

Должен отобразиться JSON с telegram и vk.

### 9.4. Выход из PostgreSQL
```sql
\q
```

---

## Шаг 10: Запуск автоматических тестов

### 10.1. Запуск всех тестов
```bash
./gradlew test
```

### 10.2. Запуск тестов с отчетом
```bash
./gradlew test --info
```

### 10.3. Просмотр отчетов о тестах
```bash
open build/reports/tests/test/index.html
```

Или на Linux:
```bash
xdg-open build/reports/tests/test/index.html
```

---

## Шаг 11: Проверка Swagger документации

### 11.1. Открыть Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

### 11.2. Открыть OpenAPI JSON
```
http://localhost:8080/v3/api-docs
```

### 11.3. Тестирование через Swagger UI

1. Нажмите на эндпоинт `/auth/signup`
2. Нажмите "Try it out"
3. Заполните JSON данные
4. Нажмите "Execute"
5. Проверьте ответ

**Для защищенных эндпоинтов:**
1. Выполните `/auth/login` или `/auth/signup`
2. Скопируйте `accessToken` из ответа
3. Нажмите кнопку "Authorize" вверху страницы
4. Введите: `Bearer YOUR_ACCESS_TOKEN`
5. Теперь можно тестировать `/profile/me`

---

## Шаг 12: Проверка логов приложения

### 12.1. Просмотр логов в консоли
Если приложение запущено через `./gradlew bootRun`, логи отображаются в консоли.

### 12.2. Поиск ошибок
Проверьте логи на наличие:
- `ERROR` - критические ошибки
- `WARN` - предупреждения
- Успешные подключения к БД и Redis

---

## Шаг 13: Проверка производительности и кэширования

### 13.1. Первый запрос профиля (промах кэша)
```bash
time curl -X GET http://localhost:8080/profile/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 13.2. Второй запрос профиля (попадание в кэш)
```bash
time curl -X GET http://localhost:8080/profile/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

Второй запрос должен быть быстрее благодаря кэшу в Redis.

---

## Шаг 14: Остановка сервисов

### 14.1. Остановка приложения
Нажмите `Ctrl+C` в терминале, где запущено приложение.

### 14.2. Остановка Docker контейнеров
```bash
cd docker
docker compose down
```

### 14.3. Остановка с удалением данных (опционально)
```bash
docker compose down -v
```

**Внимание:** Это удалит все данные из PostgreSQL и Redis!

---

## Возможные проблемы и решения

### Проблема 1: Порт уже занят
**Решение:** 
- Проверьте, что порты 8080, 25432, 6380 свободны
- Или измените порты в `application.yml` и `docker-compose.yml`

### Проблема 2: Ошибка подключения к PostgreSQL
**Решение:**
- Убедитесь, что контейнер `postgresDb` запущен: `docker compose ps`
- Проверьте логи: `docker compose logs postgres-db`

### Проблема 3: Ошибка подключения к Redis
**Решение:**
- Убедитесь, что контейнер `redis_container` запущен
- Проверьте логи: `docker compose logs redis`

### Проблема 4: ProfileMapper не найден при компиляции
**Решение:**
- Это известная проблема - файл должен быть создан вручную или MapStruct должен его сгенерировать
- Проверьте, что в `build/generated/sources/annotationProcessor/` есть сгенерированные файлы

### Проблема 5: JWT токен невалидный
**Решение:**
- Убедитесь, что используете правильный формат: `Bearer TOKEN`
- Проверьте, что токен не истек (access токен живет 15 минут)
- Используйте refresh токен для получения нового access токена

### Проблема 6: Ошибка валидации
**Решение:**
- Проверьте, что все обязательные поля заполнены (`@NotBlank` поля)
- Проверьте формат даты: `YYYY-MM-DD`

---

## Дополнительные проверки

### Проверка CORS
```bash
curl -X OPTIONS http://localhost:8080/profile/me \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -v
```

### Проверка health check (если настроен)
```bash
curl http://localhost:8080/actuator/health
```

---

## Итоговый чеклист проверки

- [ ] Инфраструктура запущена (PostgreSQL, Redis)
- [ ] Приложение успешно собрано
- [ ] Приложение запущено без ошибок
- [ ] Swagger UI доступен
- [ ] Регистрация пользователя работает
- [ ] Вход работает
- [ ] Получение профиля работает (с токеном)
- [ ] Обновление профиля работает
- [ ] Обновление токенов работает
- [ ] Выход работает
- [ ] Обработка ошибок работает (неверные данные, неавторизованный доступ)
- [ ] Кэширование работает (Redis)
- [ ] Токены хранятся в Redis
- [ ] Данные сохраняются в PostgreSQL
- [ ] Автоматические тесты проходят

---

## Заключение

После выполнения всех шагов вы должны убедиться, что:
1. Все компоненты системы работают корректно
2. Аутентификация и авторизация функционируют
3. Кэширование работает
4. Обработка ошибок работает правильно
5. API соответствует ожидаемому поведению

Если все проверки пройдены успешно, система готова к использованию!


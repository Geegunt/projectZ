# Пошаговая инструкция по запуску user-info-svc

## Проблема, которая была исправлена

Приложение не запускалось из-за отсутствующего файла `ProfileMapper.java`, который необходим для компиляции. Файл был создан.

## Требования

1. **Java 21** - должен быть установлен
2. **Docker и Docker Compose** - для запуска PostgreSQL и Redis
3. **Gradle** - уже включен в проект (gradlew)

## Шаг 1: Проверка Java

```bash
java -version
```

Должна быть версия 21 или выше. Если нет - установите Java 21.

## Шаг 2: Установка Docker (если не установлен)

### macOS:
```bash
# Через Homebrew
brew install --cask docker

# Или скачайте Docker Desktop с официального сайта:
# https://www.docker.com/products/docker-desktop
```

После установки запустите Docker Desktop и дождитесь его полной загрузки.

### Проверка установки:
```bash
docker --version
docker compose version
```

## Шаг 3: Запуск зависимостей (PostgreSQL и Redis)

Перейдите в директорию проекта:
```bash
cd /Users/glebgrigorev/Desktop/programming/projectEpsilan/user-info-svc
```

Запустите Docker Compose для поднятия PostgreSQL и Redis:
```bash
docker compose -f docker/docker-compose.yml up -d
```

Эта команда запустит:
- **PostgreSQL** на порту `25432` (внутренний порт 5432)
- **Redis** на порту `6380` (внутренний порт 6379)
- **Liquibase миграции** для создания схемы БД

### Проверка, что контейнеры запущены:
```bash
docker compose -f docker/docker-compose.yml ps
```

Вы должны увидеть 3 контейнера в статусе "Up":
- `postgresDb`
- `migrations`
- `redis_container`

### Просмотр логов (если что-то пошло не так):
```bash
docker compose -f docker/docker-compose.yml logs
```

## Шаг 4: Проверка подключения к базе данных

Убедитесь, что PostgreSQL доступен:
```bash
# Проверка через Docker
docker exec -it postgresDb psql -U userinfo -d userinfo_db -c "SELECT 1;"
```

Или через psql (если установлен локально):
```bash
psql -h localhost -p 25432 -U userinfo -d userinfo_db
# Пароль: password
```

## Шаг 5: Запуск приложения

### Вариант 1: Через Gradle (рекомендуется)
```bash
cd /Users/glebgrigorev/Desktop/programming/projectEpsilan/user-info-svc
./gradlew bootRun
```

### Вариант 2: Через собранный JAR
```bash
# Сборка JAR
./gradlew build

# Запуск
java -jar build/libs/user-info-0.0.1-SNAPSHOT.jar
```

## Шаг 6: Проверка работы приложения

После запуска приложение будет доступно на:
- **Основной порт:** `http://localhost:8080` (если не указан другой порт в application.yml)
- **Swagger UI:** `http://localhost:8080/swagger-ui`
- **API Docs:** `http://localhost:8080/v3/api-docs`

### Проверка здоровья приложения:
```bash
curl http://localhost:8080/swagger-ui
```

## Возможные проблемы и решения

### Проблема 1: "Connection refused" к PostgreSQL
**Решение:**
- Убедитесь, что Docker контейнеры запущены: `docker compose -f docker/docker-compose.yml ps`
- Проверьте, что PostgreSQL слушает на порту 25432: `docker compose -f docker/docker-compose.yml logs postgres-db`

### Проблема 2: "Connection refused" к Redis
**Решение:**
- Проверьте, что Redis контейнер запущен: `docker compose -f docker/docker-compose.yml ps redis`
- Проверьте логи: `docker compose -f docker/docker-compose.yml logs redis`

### Проблема 3: "Port already in use"
**Решение:**
- Проверьте, что порты 25432 (PostgreSQL) и 6380 (Redis) свободны
- Остановите другие приложения, использующие эти порты
- Или измените порты в `docker-compose.yml` и `application.yml`

### Проблема 4: "Schema 'user_info' does not exist"
**Решение:**
- Убедитесь, что миграции Liquibase выполнились успешно
- Проверьте логи контейнера migrations: `docker compose -f docker/docker-compose.yml logs migrations`
- Перезапустите миграции: `docker compose -f docker/docker-compose.yml up migrations`

### Проблема 5: Ошибки компиляции
**Решение:**
- Очистите проект: `./gradlew clean`
- Пересоберите: `./gradlew build`

## Остановка сервисов

### Остановка приложения:
Нажмите `Ctrl+C` в терминале, где запущено приложение.

### Остановка Docker контейнеров:
```bash
docker compose -f docker/docker-compose.yml down
```

### Остановка с удалением данных:
```bash
docker compose -f docker/docker-compose.yml down -v
```

## Полезные команды

### Просмотр логов приложения:
```bash
./gradlew bootRun
# Логи будут выводиться в консоль
```

### Просмотр логов Docker контейнеров:
```bash
# Все логи
docker compose -f docker/docker-compose.yml logs

# Логи конкретного сервиса
docker compose -f docker/docker-compose.yml logs postgres-db
docker compose -f docker/docker-compose.yml logs redis
```

### Перезапуск всех сервисов:
```bash
docker compose -f docker/docker-compose.yml restart
```

### Проверка статуса всех сервисов:
```bash
docker compose -f docker/docker-compose.yml ps
```

## Структура портов

- **8080** - Spring Boot приложение (по умолчанию)
- **25432** - PostgreSQL (внешний порт)
- **6380** - Redis (внешний порт)

## Конфигурация базы данных

- **База данных:** `userinfo_db`
- **Пользователь:** `userinfo`
- **Пароль:** `password`
- **Схема:** `user_info`

## Конфигурация Redis

- **Хост:** `localhost:6380`
- **Пользователь:** `redisuser`
- **Пароль:** `redisuserpassword`


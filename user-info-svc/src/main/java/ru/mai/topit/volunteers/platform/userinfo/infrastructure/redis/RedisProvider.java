package ru.mai.topit.volunteers.platform.userinfo.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisProvider {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String READ_CACHE_KEY = "op:cache:";
    private final ObjectMapper mapper;

    @SuppressWarnings("unchecked")
    public <T> T getAndCache(String key, Supplier<T> getDataFunc, Class<T> clazz) {
        String cacheKey = READ_CACHE_KEY + key;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                // Читаем строку и десериализуем в нужный тип
                return mapper.readValue(cached.toString(), clazz);
            } catch (Exception ex) {
                log.error("Ошибка при десериализации объекта из Redis: {}", ex.getMessage(), ex);
            }
        }

        T result = getDataFunc.get();

        if (result != null) {
            try {
                String serializedMessage = mapper.writeValueAsString(result);
                redisTemplate.opsForValue().set(cacheKey, serializedMessage);
            } catch (Exception ex) {
                log.error("Ошибка при сериализации объекта для Redis: {}", ex.getMessage(), ex);
            }
        }
        return result;
    }

    public <T> T upsertDataAndEvictCache(String key, Supplier<T> func) {
        String cacheKey = READ_CACHE_KEY + key;

        T result = func.get();

        redisTemplate.delete(cacheKey);

        return result;
    }
}

package ru.mai.topit.volunteers.platform.userinfo.infrastructure.security.jwt;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TokenStore {
    private static final String REFRESH_PREFIX = "auth:refresh:";
    private static final String BLACKLIST_PREFIX = "auth:blacklist:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProperties jwtProperties;

    public TokenStore(RedisTemplate<String, Object> redisTemplate, JwtProperties jwtProperties) {
        this.redisTemplate = redisTemplate;
        this.jwtProperties = jwtProperties;
    }

    public void storeRefreshToken(String userId, String refreshToken) {
        String key = REFRESH_PREFIX + userId + ":" + refreshToken;
        redisTemplate.opsForValue().set(key, true, Duration.ofSeconds(jwtProperties.getRefreshTtlSeconds()));
    }

    public boolean hasRefreshToken(String userId, String refreshToken) {
        String key = REFRESH_PREFIX + userId + ":" + refreshToken;
        return redisTemplate.hasKey(key);
    }

    public void revokeRefreshToken(String userId, String refreshToken) {
        String key = REFRESH_PREFIX + userId + ":" + refreshToken;
        redisTemplate.delete(key);
    }

    public void blacklistAccessToken(String jti, long ttlSeconds) {
        String key = BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(key, true, Duration.ofSeconds(ttlSeconds));
    }

    public boolean isAccessTokenBlacklisted(String jti) {
        String key = BLACKLIST_PREFIX + jti;
        return redisTemplate.hasKey(key);
    }
}



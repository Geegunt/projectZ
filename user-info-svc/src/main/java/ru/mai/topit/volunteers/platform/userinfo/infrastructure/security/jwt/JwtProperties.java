package ru.mai.topit.volunteers.platform.userinfo.infrastructure.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtProperties {
    private String secret;
    private long accessTtlSeconds = 900; // 15m
    private long refreshTtlSeconds = 2592000; // 30d
}



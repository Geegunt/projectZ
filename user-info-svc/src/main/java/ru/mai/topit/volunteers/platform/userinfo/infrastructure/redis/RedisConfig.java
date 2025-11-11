package ru.mai.topit.volunteers.platform.userinfo.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;


@Configuration()
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(RedisProperties properties) {
        var clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(properties.getCommandTimeoutMillis()))
                .clientOptions(ClientOptions.builder()
                        .autoReconnect(true)
                        .build())
                .build();

        final RedisStandaloneConfiguration standalone = getRedisStandaloneConfiguration(properties);
        if (properties.getPassword() != null && !properties.getPassword().isEmpty()) {
            standalone.setPassword(RedisPassword.of(properties.getPassword()));
        }

        var factory = new LettuceConnectionFactory(standalone, clientConfig);
        factory.setValidateConnection(true);
        return factory;
    }

    private static RedisStandaloneConfiguration getRedisStandaloneConfiguration(RedisProperties properties) {
        String hostPort = (properties.getHosts() == null || properties.getHosts().isEmpty())
                ? "localhost:6379"
                : properties.getHosts().get(0);

        String host;
        int port;
        int idx = hostPort.lastIndexOf(':');
        if (idx > -1) {
            host = hostPort.substring(0, idx);
            port = Integer.parseInt(hostPort.substring(idx + 1));
        } else {
            host = hostPort;
            port = 6379;
        }

        return new RedisStandaloneConfiguration(host, port);
    }

    @Bean
    public ObjectMapper objectMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        var stringSerializer = new StringRedisSerializer();
        var jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper(), Object.class);
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}



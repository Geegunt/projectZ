package ru.mai.topit.volunteers.platform.userinfo.infrastructure.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "redis")
@Getter
@Setter
public class RedisProperties {
    private List<String> hosts;
    private String user;
    private String password;
    private Pool pool = new Pool();
    private int commandTimeoutMillis = 10000;
    private int topologyRefreshSeconds = 60;
    private boolean validateClusterNodeMembership = false;

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pool {
        private int maxTotal = 8;
        private int minIdle = 0;

    }
}



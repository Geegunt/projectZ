package ru.mai.topit.volunteers.platform.userinfo.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.mai.topit.volunteers.platform.userinfo.infrastructure.security.jwt.JwtAuthFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] WHITE_LIST = {"/v3/api-docs/**", "/v3/api-docs", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/swagger-resources", "/webjars/**", "/auth/**"};

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()).sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(registry -> registry.requestMatchers(WHITE_LIST).permitAll().anyRequest().authenticated()).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Разрешаем конкретные origins для Angular (dev и production)
        // Для разработки Angular обычно работает на http://localhost:4200
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",  // Angular dev server
                "http://localhost:3000",  // Альтернативный порт для dev
                "http://127.0.0.1:4200",
                "http://127.0.0.1:3000"
        ));
        
        // Также разрешаем паттерны для гибкости (например, для разных портов)
        config.addAllowedOriginPattern("http://localhost:*");
        config.addAllowedOriginPattern("http://127.0.0.1:*");
        
        // Разрешаем все необходимые HTTP методы
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Разрешаем все необходимые заголовки
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        
        // Заголовки, которые клиент может читать
        config.setExposedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));
        
        // Разрешаем отправку credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Время кэширования preflight запросов (в секундах)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}


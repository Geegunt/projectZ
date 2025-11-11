package ru.mai.topit.volunteers.platform.userinfo.presentation.http.auth.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Map;

public class AuthDtos {
    public record RegisterRequest(
            @NotBlank String login,
            @NotBlank String password,
            String fullName,
            String personalEmail,
            String maiEmail,
            String institute,
            String studentGroup,
            LocalDate birthDate,
            String clothingSize,
            Map<String, String> social,
            String contactEmail
    ) {}

    public record LoginRequest(
            @NotBlank String login,
            @NotBlank String password
    ) {}

    public record TokenResponse(
            String accessToken,
            String refreshToken
    ) {}

    public record RefreshRequest(
            @NotBlank String refreshToken
    ) {}
}



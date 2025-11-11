package ru.mai.topit.volunteers.platform.userinfo.presentation.http.profile.dto;

import ru.mai.topit.volunteers.platform.userinfo.domain.vo.SocialNetworks;

import java.time.LocalDate;
import java.util.Map;

public class ProfileDtos {
    public record ProfileResponse(
            Long id,
            String login,
            String fullName,
            String personalEmail,
            String maiEmail,
            String institute,
            String studentGroup,
            LocalDate birthDate,
            String clothingSize,
            SocialNetworks social,
            String contactEmail,
            String role
    ) {
    }

    public record ProfileUpdateRequest(
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
}



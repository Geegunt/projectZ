package ru.mai.topit.volunteers.platform.userinfo.presentation.http.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mai.topit.volunteers.platform.userinfo.application.ProfileService;
import ru.mai.topit.volunteers.platform.userinfo.presentation.http.profile.dto.ProfileDtos;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileDtos.ProfileResponse> getMe(Authentication authentication) {
        String login = authentication.getName();
        var response = profileService.getProfileByLogin(login);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileDtos.ProfileResponse> updateMe(Authentication authentication,
                                                                @Valid @RequestBody ProfileDtos.ProfileUpdateRequest request) {
        String login = authentication.getName();
        var response = profileService.updateProfileByLogin(login, request);

        return ResponseEntity.ok(response);
    }
}



package ru.mai.topit.volunteers.platform.userinfo.presentation.http.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mai.topit.volunteers.platform.userinfo.application.AuthService;
import ru.mai.topit.volunteers.platform.userinfo.presentation.http.auth.dto.AuthDtos;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthDtos.TokenResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        Map<String, String> tokens = authService.register(request);
        return ResponseEntity.ok(new AuthDtos.TokenResponse(tokens.get("accessToken"), tokens.get("refreshToken")));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.TokenResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        Map<String, String> tokens = authService.login(request.login(), request.password());
        return ResponseEntity.ok(new AuthDtos.TokenResponse(tokens.get("accessToken"), tokens.get("refreshToken")));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDtos.TokenResponse> refresh(@Valid @RequestBody AuthDtos.RefreshRequest request) {
        Map<String, String> tokens = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(new AuthDtos.TokenResponse(tokens.get("accessToken"), tokens.get("refreshToken")));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody AuthDtos.RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}



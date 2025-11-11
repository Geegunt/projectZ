package ru.mai.topit.volunteers.platform.userinfo.application;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mai.topit.volunteers.platform.userinfo.application.exception.InvalidCredentialsException;
import ru.mai.topit.volunteers.platform.userinfo.application.exception.InvalidRefreshTokenException;
import ru.mai.topit.volunteers.platform.userinfo.application.exception.UserAlreadyExistsException;
import ru.mai.topit.volunteers.platform.userinfo.application.factory.FactoryProvider;
import ru.mai.topit.volunteers.platform.userinfo.application.factory.ModelFactory;
import ru.mai.topit.volunteers.platform.userinfo.domain.User;
import ru.mai.topit.volunteers.platform.userinfo.infrastructure.repository.UserRepository;
import ru.mai.topit.volunteers.platform.userinfo.infrastructure.security.jwt.JwtService;
import ru.mai.topit.volunteers.platform.userinfo.infrastructure.security.jwt.TokenStore;
import ru.mai.topit.volunteers.platform.userinfo.presentation.http.auth.dto.AuthDtos;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenStore tokenStore;
    private final FactoryProvider factoryProvider;

    @Transactional
    public Map<String, String> register(AuthDtos.RegisterRequest request) {
        String login = request.login();

        if (userRepository.existsByLogin(login)) {
            throw new UserAlreadyExistsException(login);
        }

        ModelFactory<AuthDtos.RegisterRequest, User> userFactory = factoryProvider.getFactory(AuthDtos.RegisterRequest.class);
        User user = userFactory.create(request);
        user = userRepository.save(user);

        return createJwtToken(user);
    }

    @Transactional(readOnly = true)
    public Map<String, String> login(String login, String rawPassword) {
        User user = userRepository.findByLogin(login).orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return createJwtToken(user);
    }

    private Map<String, String> createJwtToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        String access = jwtService.issueAccessToken(user.getLogin(), claims);
        String refresh = jwtService.issueRefreshToken(user.getLogin());
        tokenStore.storeRefreshToken(String.valueOf(user.getId()), refresh);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", access);
        tokens.put("refreshToken", refresh);
        return tokens;
    }

    @Transactional
    public Map<String, String> refresh(String refreshToken) {
        Claims claims = jwtService.parseAndValidate(refreshToken);
        String login = claims.getSubject();
        User user = userRepository.findByLogin(login).orElseThrow(InvalidRefreshTokenException::new);
        if (!tokenStore.hasRefreshToken(String.valueOf(user.getId()), refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("uid", user.getId());

        String newAccess = jwtService.issueAccessToken(login, newClaims);
        String newRefresh = jwtService.issueRefreshToken(login);

        tokenStore.revokeRefreshToken(String.valueOf(user.getId()), refreshToken);
        tokenStore.storeRefreshToken(String.valueOf(user.getId()), newRefresh);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccess);
        tokens.put("refreshToken", newRefresh);
        return tokens;
    }

    @Transactional
    public void logout(String refreshToken) {
        Claims claims = jwtService.parseAndValidate(refreshToken);
        String login = claims.getSubject();
        User user = userRepository.findByLogin(login).orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        tokenStore.revokeRefreshToken(String.valueOf(user.getId()), refreshToken);
    }
}



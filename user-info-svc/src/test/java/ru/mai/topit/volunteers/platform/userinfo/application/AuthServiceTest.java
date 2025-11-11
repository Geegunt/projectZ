package ru.mai.topit.volunteers.platform.userinfo.application;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import ru.mai.topit.volunteers.platform.userinfo.support.fixtures.TestFixtures;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    JwtService jwtService;
    @Mock
    TokenStore tokenStore;
    @Mock
    FactoryProvider factoryProvider;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    ModelFactory<AuthDtos.RegisterRequest, User> userFactory;

    AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, tokenStore, factoryProvider);
    }

    @Test
    void register_success_createsUserAndReturnsTokens() {
        AuthDtos.RegisterRequest req = TestFixtures.registerRequest();
        when(userRepository.existsByLogin(req.login())).thenReturn(false);
        doReturn(userFactory).when(factoryProvider).getFactory(AuthDtos.RegisterRequest.class);
        User toSave = TestFixtures.user(null, req.login());
        when(userFactory.create(req)).thenReturn(toSave);
        User saved = TestFixtures.user(10L, req.login());
        when(userRepository.save(toSave)).thenReturn(saved);
        when(jwtService.issueAccessToken(eq(saved.getLogin()), anyMap())).thenReturn("access");
        when(jwtService.issueRefreshToken(saved.getLogin())).thenReturn("refresh");

        Map<String, String> tokens = authService.register(req);

        assertEquals("access", tokens.get("accessToken"));
        assertEquals("refresh", tokens.get("refreshToken"));
        verify(tokenStore).storeRefreshToken(String.valueOf(saved.getId()), "refresh");
    }

    @Test
    void register_existingLogin_throws() {
        AuthDtos.RegisterRequest req = TestFixtures.registerRequest();
        when(userRepository.existsByLogin(req.login())).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(req));
        verifyNoInteractions(factoryProvider);
    }

    @Test
    void login_success_returnsTokens() {
        User user = TestFixtures.user(5L, "john");
        when(userRepository.findByLogin("john")).thenReturn(Optional.of(user));
        when(jwtService.issueAccessToken(eq("john"), anyMap())).thenReturn("access");
        when(jwtService.issueRefreshToken("john")).thenReturn("refresh");
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        Map<String, String> tokens = authService.login("john", "irrelevant");

        assertEquals("access", tokens.get("accessToken"));
        assertEquals("refresh", tokens.get("refreshToken"));
        verify(tokenStore).storeRefreshToken("5", "refresh");
    }

    @Test
    void login_userNotFound_throws() {
        when(userRepository.findByLogin("john")).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> authService.login("john", "pass"));
    }

    @Test
    void refresh_success_issuesNewTokens() {
        Claims claims = mock(Claims.class);
        when(jwtService.parseAndValidate("oldRefresh")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("john");
        User user = TestFixtures.user(7L, "john");
        when(userRepository.findByLogin("john")).thenReturn(Optional.of(user));
        when(tokenStore.hasRefreshToken("7", "oldRefresh")).thenReturn(true);
        when(jwtService.issueAccessToken(eq("john"), anyMap())).thenReturn("newAccess");
        when(jwtService.issueRefreshToken("john")).thenReturn("newRefresh");

        Map<String, String> tokens = authService.refresh("oldRefresh");

        assertEquals("newAccess", tokens.get("accessToken"));
        assertEquals("newRefresh", tokens.get("refreshToken"));
        verify(tokenStore).revokeRefreshToken("7", "oldRefresh");
        verify(tokenStore).storeRefreshToken("7", "newRefresh");
    }

    @Test
    void refresh_invalidToken_throws() {
        Claims claims = mock(Claims.class);
        when(jwtService.parseAndValidate("bad")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("john");
        when(userRepository.findByLogin("john")).thenReturn(Optional.empty());
        assertThrows(InvalidRefreshTokenException.class, () -> authService.refresh("bad"));
    }

    @Test
    void logout_revokesToken() {
        Claims claims = mock(Claims.class);
        when(jwtService.parseAndValidate("refresh")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("john");
        User user = TestFixtures.user(3L, "john");
        when(userRepository.findByLogin("john")).thenReturn(Optional.of(user));

        authService.logout("refresh");

        verify(tokenStore).revokeRefreshToken("3", "refresh");
    }
}



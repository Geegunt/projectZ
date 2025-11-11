package ru.mai.topit.volunteers.platform.userinfo.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mai.topit.volunteers.platform.userinfo.application.exception.UserNotFoundException;
import ru.mai.topit.volunteers.platform.userinfo.application.mapper.ProfileMapper;
import ru.mai.topit.volunteers.platform.userinfo.domain.User;
import ru.mai.topit.volunteers.platform.userinfo.domain.vo.SocialNetworks;
import ru.mai.topit.volunteers.platform.userinfo.domain.vo.UserRole;
import ru.mai.topit.volunteers.platform.userinfo.infrastructure.redis.RedisProvider;
import ru.mai.topit.volunteers.platform.userinfo.infrastructure.repository.UserRepository;
import ru.mai.topit.volunteers.platform.userinfo.presentation.http.profile.dto.ProfileDtos;
import ru.mai.topit.volunteers.platform.userinfo.support.fixtures.TestFixtures;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ProfileMapper profileMapper;
    @Mock
    RedisProvider redisProvider;

    ProfileService profileService;

    @BeforeEach
    void setUp() {
        profileService = new ProfileService(userRepository, profileMapper, redisProvider);
    }

    @Test
    void getProfileByLogin_success_returnsDtoFromCacheOrDb() {
        String login = "john";
        ProfileDtos.ProfileResponse dto = new ProfileDtos.ProfileResponse(1L, login, "John Doe", "john@ex.com", "u@mai.ru", "IT", "G", LocalDate.of(2000, 1, 1), "L", new SocialNetworks("telegram", "@tg"), "john.contact@ex.com", UserRole.VOLUNTEER.name());

        when(redisProvider.getAndCache(eq("user:profile:" + login), any(), any())).thenAnswer(inv -> {
            Supplier<ProfileDtos.ProfileResponse> supplier = inv.getArgument(1);
            return supplier.get();
        });
        User user = TestFixtures.user(1L, login);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(profileMapper.toDto(user)).thenReturn(dto);

        ProfileDtos.ProfileResponse result = profileService.getProfileByLogin(login);

        assertEquals(dto, result);
    }

    @Test
    void getProfileByLogin_userNotFound_throws() {
        String login = "missing";
        when(redisProvider.getAndCache(eq("user:profile:" + login), any(), any())).thenAnswer(inv -> {
            Supplier<ProfileDtos.ProfileResponse> supplier = inv.getArgument(1);
            return supplier.get();
        });
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> profileService.getProfileByLogin(login));
    }

    @Test
    void updateProfileByLogin_success_updatesAndEvicts() {
        String login = "john";
        ProfileDtos.ProfileUpdateRequest req = new ProfileDtos.ProfileUpdateRequest("John", null, null, null, null, null, null, null, null);
        User user = TestFixtures.user(1L, login);
        User saved = TestFixtures.user(1L, login);
        ProfileDtos.ProfileResponse dto = new ProfileDtos.ProfileResponse(1L, login, "John", "john@ex.com", "u@mai.ru", "IT", "G", LocalDate.of(2000, 1, 1), "L", new SocialNetworks("telegram", "@tg"), "john.contact@ex.com", UserRole.MODERATOR.name());

        when(redisProvider.upsertDataAndEvictCache(eq("user:profile:" + login), any())).thenAnswer(inv -> {
            Supplier<ProfileDtos.ProfileResponse> supplier = inv.getArgument(1);
            return supplier.get();
        });
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(saved);
        when(profileMapper.toDto(saved)).thenReturn(dto);

        ProfileDtos.ProfileResponse result = profileService.updateProfileByLogin(login, req);

        assertEquals(dto, result);
        verify(profileMapper).updateEntityFromRequest(req, user);
    }

    @Test
    void updateProfileByLogin_userNotFound_throws() {
        String login = "missing";
        ProfileDtos.ProfileUpdateRequest req = new ProfileDtos.ProfileUpdateRequest(null, null, null, null, null, null, null, null, null);
        when(redisProvider.upsertDataAndEvictCache(eq("user:profile:" + login), any())).thenAnswer(inv -> {
            Supplier<ProfileDtos.ProfileResponse> supplier = inv.getArgument(1);
            return supplier.get();
        });
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> profileService.updateProfileByLogin(login, req));
    }
}



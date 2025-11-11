package ru.mai.topit.volunteers.platform.userinfo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mai.topit.volunteers.platform.userinfo.application.exception.UserNotFoundException;
import ru.mai.topit.volunteers.platform.userinfo.application.mapper.ProfileMapper;
import ru.mai.topit.volunteers.platform.userinfo.domain.User;
import ru.mai.topit.volunteers.platform.userinfo.infrastructure.redis.RedisProvider;
import ru.mai.topit.volunteers.platform.userinfo.infrastructure.repository.UserRepository;
import ru.mai.topit.volunteers.platform.userinfo.presentation.http.profile.dto.ProfileDtos;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final RedisProvider redisProvider;

    public ProfileDtos.ProfileResponse getProfileByLogin(String login) {
        String key = "user:profile:" + login;

        return redisProvider.getAndCache(key, () ->
                        userRepository.findByLogin(login)
                                .map(profileMapper::toDto)
                                .orElseThrow(() -> new UserNotFoundException(login)),
                ProfileDtos.ProfileResponse.class
        );
    }

    public ProfileDtos.ProfileResponse updateProfileByLogin(String login, ProfileDtos.ProfileUpdateRequest request) {
        String key = "user:profile:" + login;

        return redisProvider.upsertDataAndEvictCache(key, () -> {
            User user = userRepository.findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));
            profileMapper.updateEntityFromRequest(request, user);
            User saved = userRepository.save(user);
            return profileMapper.toDto(saved);
        });
    }
}



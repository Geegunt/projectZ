package ru.mai.topit.volunteers.platform.userinfo.application.factory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.mai.topit.volunteers.platform.userinfo.application.mapper.UserMapper;
import ru.mai.topit.volunteers.platform.userinfo.domain.User;
import ru.mai.topit.volunteers.platform.userinfo.presentation.http.auth.dto.AuthDtos;

import java.time.OffsetDateTime;

@Component
public class UserFactory implements ModelFactory<AuthDtos.RegisterRequest, User> {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserFactory(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Class<AuthDtos.RegisterRequest> supportsSource() {
        return AuthDtos.RegisterRequest.class;
    }

    @Override
    public User create(AuthDtos.RegisterRequest source) {
        User user = userMapper.toEntity(source);
        user.setPassword(passwordEncoder.encode(source.password()));
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        return user;
    }
}



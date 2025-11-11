package ru.mai.topit.volunteers.platform.userinfo.application.factory;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mai.topit.volunteers.platform.userinfo.application.mapper.UserMapper;
import ru.mai.topit.volunteers.platform.userinfo.domain.User;
import ru.mai.topit.volunteers.platform.userinfo.presentation.http.auth.dto.AuthDtos;
import ru.mai.topit.volunteers.platform.userinfo.support.fixtures.TestFixtures;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserFactoryTest {

    @Test
    void create_mapsFields_and_encodesPassword_setsTimestamps() {
        UserMapper mapper = mock(UserMapper.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        UserFactory factory = new UserFactory(mapper, encoder);
        AuthDtos.RegisterRequest req = TestFixtures.registerRequest();
        User mapped = new User();
        mapped.setLogin(req.login());
        when(mapper.toEntity(req)).thenReturn(mapped);
        when(encoder.encode("pass")).thenReturn("ENC");

        User user = factory.create(req);

        assertEquals("ENC", user.getPassword());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertEquals(req.login(), user.getLogin());
    }
}



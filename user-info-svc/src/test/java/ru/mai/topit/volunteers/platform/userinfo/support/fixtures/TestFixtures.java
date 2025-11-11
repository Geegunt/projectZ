package ru.mai.topit.volunteers.platform.userinfo.support.fixtures;

import ru.mai.topit.volunteers.platform.userinfo.domain.User;
import ru.mai.topit.volunteers.platform.userinfo.presentation.http.auth.dto.AuthDtos;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class TestFixtures {
    public static AuthDtos.RegisterRequest registerRequest() {
        Map<String, String> social = new HashMap<>();
        social.put("telegram", "@tg");
        social.put("vk", "vk.com/user");
        return new AuthDtos.RegisterRequest(
                "john", "pass", "John Doe", "john@ex.com",
                "u12345@mai.ru", "IT", "M8O-101B-23",
                LocalDate.of(2000, 1, 1), "L", social, "john.contact@ex.com"
        );
    }

    public static User user(Long id, String login) {
        User u = new User();
        u.setId(id);
        u.setLogin(login);
        u.setPassword("encoded");
        u.setFullName("John Doe");
        u.setPersonalEmail("john@ex.com");
        u.setMaiEmail("u12345@mai.ru");
        u.setInstitute("IT");
        u.setStudentGroup("M8O-101B-23");
        u.setBirthDate(LocalDate.of(2000,1,1));
        u.setClothingSize("L");
        Map<String,String> social = new HashMap<>();
        social.put("telegram", "@tg");
        social.put("vk", "vk.com/user");
        u.setSocial(social);
        u.setContactEmail("john.contact@ex.com");
        u.setCreatedAt(OffsetDateTime.now());
        u.setUpdatedAt(OffsetDateTime.now());
        return u;
    }
}



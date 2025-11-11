package ru.mai.topit.volunteers.platform.userinfo.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mai.topit.volunteers.platform.userinfo.domain.User;
import ru.mai.topit.volunteers.platform.userinfo.presentation.http.auth.dto.AuthDtos;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(AuthDtos.RegisterRequest request);
}



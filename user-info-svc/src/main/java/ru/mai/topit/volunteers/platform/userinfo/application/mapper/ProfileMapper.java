package ru.mai.topit.volunteers.platform.userinfo.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mai.topit.volunteers.platform.userinfo.domain.User;
import ru.mai.topit.volunteers.platform.userinfo.presentation.http.profile.dto.ProfileDtos;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    
    @Mapping(target = "social", source = "socialNetworks")
    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : \"volunteer\")")
    ProfileDtos.ProfileResponse toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "login", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "social", source = "social")
    void updateEntityFromRequest(ProfileDtos.ProfileUpdateRequest request, @MappingTarget User user);
}


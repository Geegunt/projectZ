package ru.mai.topit.volunteers.platform.userinfo.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.mai.topit.volunteers.platform.userinfo.domain.vo.UserRole;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbValue();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return UserRole.fromDbValue(dbData);
    }
}

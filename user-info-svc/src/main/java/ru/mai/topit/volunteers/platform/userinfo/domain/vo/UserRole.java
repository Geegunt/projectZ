package ru.mai.topit.volunteers.platform.userinfo.domain.vo;


import lombok.Getter;

@Getter
public enum UserRole {
    VOLUNTEER("volunteer"),
    MODERATOR("moderator");

    private final String dbValue;

    UserRole(String dbValue) {
        this.dbValue = dbValue;
    }

    public static UserRole fromDbValue(String dbValue) {
        for (UserRole role : values()) {
            if (role.dbValue.equalsIgnoreCase(dbValue)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown value for UserRole: " + dbValue);
    }
}

package ru.mai.topit.volunteers.platform.userinfo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import ru.mai.topit.volunteers.platform.userinfo.domain.vo.SocialNetworks;
import ru.mai.topit.volunteers.platform.userinfo.domain.vo.UserRole;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "user_info")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "login", nullable = false, length = Integer.MAX_VALUE)
    private String login;

    @NotNull
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Column(name = "personal_email", length = Integer.MAX_VALUE)
    private String personalEmail;

    @Column(name = "mai_email", length = Integer.MAX_VALUE)
    private String maiEmail;

    @Column(name = "full_name", length = Integer.MAX_VALUE)
    private String fullName;

    @Column(name = "institute", length = Integer.MAX_VALUE)
    private String institute;

    @Column(name = "student_group", length = Integer.MAX_VALUE)
    private String studentGroup;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "clothing_size", length = Integer.MAX_VALUE)
    private String clothingSize;

    @NotNull
    @ColumnDefault("'{}'")
    @Column(name = "social", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> social;

    @Column(name = "contact_email", length = Integer.MAX_VALUE)
    private String contactEmail;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(nullable = false, columnDefinition = "user_role")
    @ColumnDefault("'volunteer'")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private UserRole role;

    public SocialNetworks getSocialNetworks() {
        return new SocialNetworks(
                social.get("telegram"),
                social.get("vk")
        );
    }
}
package ru.mai.topit.volunteers.platform.userinfo.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mai.topit.volunteers.platform.userinfo.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
}



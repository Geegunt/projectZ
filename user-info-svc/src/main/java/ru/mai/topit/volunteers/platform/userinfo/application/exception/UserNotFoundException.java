package ru.mai.topit.volunteers.platform.userinfo.application.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String login) {
        super("User not found: " + login);
    }
}


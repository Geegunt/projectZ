package ru.mai.topit.volunteers.platform.userinfo.application.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String login) {
        super("User with login '" + login + "' already exists");
    }
}



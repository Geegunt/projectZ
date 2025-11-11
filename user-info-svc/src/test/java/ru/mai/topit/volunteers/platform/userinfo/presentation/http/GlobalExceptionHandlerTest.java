package ru.mai.topit.volunteers.platform.userinfo.presentation.http;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.mai.topit.volunteers.platform.userinfo.application.exception.InvalidCredentialsException;
import ru.mai.topit.volunteers.platform.userinfo.application.exception.UserAlreadyExistsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUserAlreadyExists_returnsConflict() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        ResponseEntity<Object> resp = handler.handleUserAlreadyExists(new UserAlreadyExistsException("john"), req);
        Map<?,?> body = (Map<?,?>) resp.getBody();
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals("USER_EXISTS", body.get("code"));
    }

    @Test
    void handleInvalidCredentials_returnsUnauthorized() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        ResponseEntity<Object> resp = handler.handleInvalidCredentials(new InvalidCredentialsException(), req);
        Map<?,?> body = (Map<?,?>) resp.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertEquals("INVALID_CREDENTIALS", body.get("code"));
    }
}



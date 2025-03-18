package com.example.hubspotintegration.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://meetimecaseteste.loca.lt/install");
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<String> handleUserExistException(UserExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}

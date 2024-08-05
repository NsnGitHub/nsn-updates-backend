package com.nsn.nsnupdatesbackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleException(BadCredentialsException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.UNAUTHORIZED, exception.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(apiException, HttpStatus.UNAUTHORIZED);
    }
}

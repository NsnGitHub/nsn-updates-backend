package com.nsn.nsnupdatesbackend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleException(BadCredentialsException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.UNAUTHORIZED,
            exception.getMessage(), ZonedDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handleException(MissingRequestHeaderException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.BAD_REQUEST,
                exception.getMessage(), ZonedDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleException(UsernameNotFoundException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.BAD_REQUEST,
                exception.getMessage(), ZonedDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<?> handleException(APIException apiException, HttpServletRequest request) {
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

}

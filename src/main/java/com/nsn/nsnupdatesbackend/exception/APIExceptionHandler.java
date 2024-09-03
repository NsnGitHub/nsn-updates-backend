package com.nsn.nsnupdatesbackend.exception;

import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;


@RestControllerAdvice
public class APIExceptionHandler {

    private final View error;

    public APIExceptionHandler(View error) {
        this.error = error;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleException(BadCredentialsException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.UNAUTHORIZED,
            exception.getMessage());
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handleException(MissingRequestHeaderException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.BAD_REQUEST,
                exception.getMessage());
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleException(EntityNotFoundException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.BAD_REQUEST,
                exception.getMessage());
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<?> handleException(EntityExistsException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.CONFLICT,
                exception.getMessage());
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleException(SignatureException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.UNAUTHORIZED,
                "Invalid Token");
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleException(AccessDeniedException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.FORBIDDEN,
            exception.getMessage());
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleException(BadRequestException exception, HttpServletRequest request) {
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.BAD_REQUEST,
            exception.getMessage());
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String errorMessage = exception.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.BAD_REQUEST,
            errorMessage);
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<?> handleException(APIException apiException) {
        return ResponseEntity.status(apiException.getStatus()).body(apiException);
    }

}

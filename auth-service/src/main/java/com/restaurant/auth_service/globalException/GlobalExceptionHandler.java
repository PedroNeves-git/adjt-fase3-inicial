package com.restaurant.auth_service.globalException;

import com.restaurant.auth_service.common.dto.ErrorResponse;
import com.restaurant.auth_service.common.dto.ErrorResponseBadRequest;
import com.restaurant.auth_service.core.exception.EmailAlreadyInUseException;
import com.restaurant.auth_service.core.exception.InvalidFieldException;
import com.restaurant.auth_service.core.exception.InvalidRoleException;
import com.restaurant.auth_service.core.exception.UserNotFoundException;
import com.restaurant.auth_service.infra.security.exception.BadCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<ErrorResponseBadRequest> handleInvalidField(InvalidFieldException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseBadRequest(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getCode(),
                        ex.getField(),
                        ex.getMessage(),
                        OffsetDateTime.now()
                )
        );
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyInUse(EmailAlreadyInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        "EMAIL_ALREADY_IN_USE",
                        ex.getMessage(),
                        OffsetDateTime.now()
                )
        );
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRole(InvalidRoleException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        "INVALID_ROLE",
                        ex.getMessage(),
                        OffsetDateTime.now()
                )
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        "USER_NOT_FOUND",
                        ex.getMessage(),
                        OffsetDateTime.now()
                )
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        "INVALID_CREDENTIALS",
                        e.getMessage(),
                        OffsetDateTime.now()
                )
        );
    }
}

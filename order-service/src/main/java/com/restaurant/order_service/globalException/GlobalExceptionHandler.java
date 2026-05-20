package com.restaurant.order_service.globalException;

import com.restaurant.order_service.common.dto.ErrorResponse;
import com.restaurant.order_service.menu.core.exception.MenuItemNotFoundException;
import com.restaurant.order_service.menu.core.exception.MenuItemUnavailableException;
import com.restaurant.order_service.order.core.exception.InvalidOrderStateException;
import com.restaurant.order_service.order.core.exception.OrderAccessDeniedException;
import com.restaurant.order_service.order.core.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({ OrderNotFoundException.class, MenuItemNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getClass().getSimpleName(), ex.getMessage(), req);
    }

    @ExceptionHandler(OrderAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(OrderAccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "OrderAccessDenied", ex.getMessage(), req);
    }

    @ExceptionHandler(MenuItemUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUnavailable(MenuItemUnavailableException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "MenuItemUnavailable", ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidOrderStateException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "InvalidOrderState", ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        ErrorResponse body = ErrorResponse.withDetails(
                HttpStatus.BAD_REQUEST.value(),
                "ValidationError",
                "Request validation failed",
                req.getRequestURI(),
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "MalformedRequest", "Request body is malformed or missing", req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "IllegalArgument", ex.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnyOther(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception on {}", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "InternalServerError", "An unexpected error occurred", req);
    }

    private static ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(
                ErrorResponse.of(status.value(), error, message, req.getRequestURI())
        );
    }
}

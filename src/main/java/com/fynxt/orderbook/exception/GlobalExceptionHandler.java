package com.fynxt.orderbook.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            InsufficientSharesException.class,
            PendingOrderLimitExceededException.class,
            InvalidOrderStateException.class
    })
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException exception) {
        log.warn("Handled exception class=GlobalExceptionHandler method=handleConflict status={} exception={} message={}",
                HttpStatus.CONFLICT.value(), exception.getClass().getSimpleName(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException exception) {
        log.warn("Handled exception class=GlobalExceptionHandler method=handleNotFound status={} exception={} message={}",
                HttpStatus.NOT_FOUND.value(), exception.getClass().getSimpleName(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        String fieldError = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse(null);
        String message = fieldError != null ? fieldError : exception.getBindingResult().getGlobalErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() == null ? "Invalid request" : error.getDefaultMessage())
                .orElse("Invalid request");
        log.warn("Handled exception class=GlobalExceptionHandler method=handleValidation status={} message={}",
                HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .orElse("Invalid request parameter");
        log.warn("Handled exception class=GlobalExceptionHandler method=handleConstraintViolation status={} message={}",
                HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Map<String, String>> handleMalformedRequest(Exception exception) {
        log.warn("Handled exception class=GlobalExceptionHandler method=handleMalformedRequest status={} exception={}",
                HttpStatus.BAD_REQUEST.value(), exception.getClass().getSimpleName());
        return ResponseEntity.badRequest().body(Map.of(
                "message", "Invalid request value. Check enum values and parameter types."
        ));
    }
}

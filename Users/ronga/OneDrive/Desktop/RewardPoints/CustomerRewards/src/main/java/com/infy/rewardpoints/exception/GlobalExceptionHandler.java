package com.infy.rewardpoints.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(GlobalExceptionHandler::formatFieldError)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errorBody("Validation failed", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errorBody("Validation failed", errors));

    }

    @ExceptionHandler(RewardPointsException.class)
    public ResponseEntity<Map<String, Object>> handleRewardPointsException(RewardPointsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody("Request failed", List.of(ex.getMessage())));

    }

    private static String formatFieldError(FieldError error) {
        if (error.getField() == null || error.getField().isBlank()) {
            return error.getDefaultMessage();
        }
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private static Map<String, Object> errorBody(String message, List<String> errors) {
                Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("errors", errors);
        return body;
    }
}

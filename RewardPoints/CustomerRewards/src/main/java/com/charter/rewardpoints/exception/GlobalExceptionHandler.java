package com.charter.rewardpoints.exception;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
        return ResponseEntity.badRequest().body(errorBody("There is a constraint violation", errors));

    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = ex.getName() + " should be of type " + ex.getRequiredType().getSimpleName();
        return ResponseEntity.badRequest()
                .body(errorBody("Bad Request", List.of(error)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        String error = "Required parameter '" + ex.getParameterName() + "' is missing";
        return ResponseEntity.badRequest()
                .body(errorBody("Bad Request", List.of(error)));
    }
    
    @ExceptionHandler(RewardPointsException.class)
    public ResponseEntity<Map<String, Object>> handleRewardPointsException(RewardPointsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody("The request cannot be processed", List.of(ex.getMessage())));

    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        String error = "An unexpected error occurred: Please contact support."; 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("Internal Server Error", List.of(error)));
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

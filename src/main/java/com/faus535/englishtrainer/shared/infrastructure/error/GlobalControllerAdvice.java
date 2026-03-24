package com.faus535.englishtrainer.shared.infrastructure.error;

import com.faus535.englishtrainer.shared.domain.error.AlreadyExistsException;
import com.faus535.englishtrainer.shared.domain.error.InvalidValueException;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import com.faus535.englishtrainer.user.domain.error.ProfileOwnershipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", ex.getMessage()));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    ResponseEntity<ApiError> handleAlreadyExists(AlreadyExistsException ex) {
        log.error("Resource already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("already_exists", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiError("validation_error", "Invalid request data"));
    }

    @ExceptionHandler(InvalidValueException.class)
    ResponseEntity<ApiError> handleInvalidValue(InvalidValueException ex) {
        log.error("Invalid value: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("invalid_value", ex.getMessage()));
    }

    @ExceptionHandler(ProfileOwnershipException.class)
    ResponseEntity<ApiError> handleOwnership(ProfileOwnershipException ex) {
        log.warn("Profile ownership violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError("forbidden", "Cannot access another user's profile"));
    }
}

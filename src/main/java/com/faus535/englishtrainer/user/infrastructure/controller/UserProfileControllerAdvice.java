package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.user")
class UserProfileControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(UserProfileControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(UserProfileNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(UserProfileNotFoundException ex) {
        log.error("User profile not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", "User profile not found"));
    }

    @ExceptionHandler(InvalidModuleException.class)
    ResponseEntity<ApiError> handleInvalidModule(InvalidModuleException ex) {
        log.error("Invalid module: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("invalid_module", ex.getMessage()));
    }

    @ExceptionHandler(InvalidXpAmountException.class)
    ResponseEntity<ApiError> handleInvalidXpAmount(InvalidXpAmountException ex) {
        log.error("Invalid XP amount: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("invalid_xp_amount", ex.getMessage()));
    }
}

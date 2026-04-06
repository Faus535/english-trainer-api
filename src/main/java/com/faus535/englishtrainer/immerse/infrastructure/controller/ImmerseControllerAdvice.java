package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.domain.error.ImmerseAiException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentAccessDeniedException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotProcessedException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseExerciseNotFoundException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.immerse")
class ImmerseControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ImmerseControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(ImmerseContentAccessDeniedException.class)
    ResponseEntity<ApiError> handleAccessDenied(ImmerseContentAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError("access_denied", ex.getMessage()));
    }

    @ExceptionHandler(ImmerseContentNotFoundException.class)
    ResponseEntity<ApiError> handleContentNotFound(ImmerseContentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("not_found", ex.getMessage()));
    }

    @ExceptionHandler(ImmerseExerciseNotFoundException.class)
    ResponseEntity<ApiError> handleExerciseNotFound(ImmerseExerciseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("not_found", ex.getMessage()));
    }

    @ExceptionHandler(ImmerseContentNotProcessedException.class)
    ResponseEntity<ApiError> handleNotProcessed(ImmerseContentNotProcessedException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiError("not_processed", ex.getMessage()));
    }

    @ExceptionHandler(UserProfileNotFoundException.class)
    ResponseEntity<ApiError> handleProfileNotFound(UserProfileNotFoundException ex) {
        log.warn("Immerse operation for user with missing profile: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("profile_not_found", ex.getMessage()));
    }

    @ExceptionHandler(ImmerseAiException.class)
    ResponseEntity<ApiError> handleAiError(ImmerseAiException ex) {
        log.error("Immerse AI error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiError("ai_unavailable", "Content processing temporarily unavailable"));
    }
}

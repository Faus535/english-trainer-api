package com.faus535.englishtrainer.dailychallenge.infrastructure.controller;

import com.faus535.englishtrainer.dailychallenge.domain.error.ChallengeNotFoundException;
import com.faus535.englishtrainer.dailychallenge.domain.error.DailyChallengeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.dailychallenge")
class DailyChallengeControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(DailyChallengeControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(ChallengeNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(ChallengeNotFoundException ex) {
        log.error("Challenge not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", ex.getMessage()));
    }

    @ExceptionHandler(DailyChallengeException.class)
    ResponseEntity<ApiError> handleDailyChallengeException(DailyChallengeException ex) {
        log.error("Daily challenge error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("daily_challenge_error", ex.getMessage()));
    }
}

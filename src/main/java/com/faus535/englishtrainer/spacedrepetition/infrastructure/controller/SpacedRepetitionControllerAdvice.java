package com.faus535.englishtrainer.spacedrepetition.infrastructure.controller;

import com.faus535.englishtrainer.spacedrepetition.domain.error.SpacedRepetitionItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.spacedrepetition")
class SpacedRepetitionControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(SpacedRepetitionControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(SpacedRepetitionItemNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(SpacedRepetitionItemNotFoundException ex) {
        log.error("Spaced repetition item not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", "Review item not found"));
    }
}

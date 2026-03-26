package com.faus535.englishtrainer.vocabulary.infrastructure.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.vocabulary")
class VocabularyControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(VocabularyControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Invalid vocabulary argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("invalid_argument", ex.getMessage()));
    }
}

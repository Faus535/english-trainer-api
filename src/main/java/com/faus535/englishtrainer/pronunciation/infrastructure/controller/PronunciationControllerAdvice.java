package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.pronunciation")
class PronunciationControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(PronunciationControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(PronunciationException.class)
    ResponseEntity<ApiError> handlePronunciationException(PronunciationException ex) {
        log.error("Pronunciation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("pronunciation_error", ex.getMessage()));
    }
}

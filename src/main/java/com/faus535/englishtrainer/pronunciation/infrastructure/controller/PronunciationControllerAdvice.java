package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationDrillNotFoundException;
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

    @ExceptionHandler(PronunciationAiException.class)
    ResponseEntity<ApiError> handleAiError(PronunciationAiException ex) {
        log.error("Pronunciation AI error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiError("ai_unavailable", "AI service temporarily unavailable"));
    }

    @ExceptionHandler(PronunciationDrillNotFoundException.class)
    ResponseEntity<ApiError> handleDrillNotFound(PronunciationDrillNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", "Pronunciation drill not found"));
    }

}

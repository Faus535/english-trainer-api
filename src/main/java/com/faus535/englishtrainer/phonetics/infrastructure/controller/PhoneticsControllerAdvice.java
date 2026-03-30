package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.domain.error.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.phonetics")
class PhoneticsControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(PhoneticsControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(PhonemeNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(PhonemeNotFoundException ex) {
        log.error("Phoneme not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", ex.getMessage()));
    }

    @ExceptionHandler(NoPhonemesAvailableException.class)
    ResponseEntity<ApiError> handleNoPhonemesAvailable(NoPhonemesAvailableException ex) {
        log.error("No phonemes available: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("no_phonemes_available", ex.getMessage()));
    }

    @ExceptionHandler(PhonemeAlreadyCompletedException.class)
    ResponseEntity<ApiError> handleAlreadyCompleted(PhonemeAlreadyCompletedException ex) {
        log.warn("Phoneme already completed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("already_completed", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientPhrasesCompletedException.class)
    ResponseEntity<ApiError> handleInsufficientPhrases(InsufficientPhrasesCompletedException ex) {
        log.warn("Insufficient phrases completed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiError("insufficient_phrases", ex.getMessage()));
    }

    @ExceptionHandler(PhoneticsException.class)
    ResponseEntity<ApiError> handlePhoneticsException(PhoneticsException ex) {
        log.error("Phonetics error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("phonetics_error", ex.getMessage()));
    }
}

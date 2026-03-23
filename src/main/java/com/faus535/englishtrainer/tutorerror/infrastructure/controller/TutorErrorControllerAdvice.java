package com.faus535.englishtrainer.tutorerror.infrastructure.controller;

import com.faus535.englishtrainer.tutorerror.domain.error.TutorErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.tutorerror")
class TutorErrorControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(TutorErrorControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(TutorErrorException.class)
    ResponseEntity<ApiError> handleTutorError(TutorErrorException ex) {
        log.error("Tutor error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("tutor_error", ex.getMessage()));
    }
}

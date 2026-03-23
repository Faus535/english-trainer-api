package com.faus535.englishtrainer.assessment.infrastructure.controller;

import com.faus535.englishtrainer.assessment.application.TestCooldownException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.assessment")
class AssessmentControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(AssessmentControllerAdvice.class);

    record ApiError(String code, String message, String availableAt) {}

    @ExceptionHandler(TestCooldownException.class)
    ResponseEntity<ApiError> handleTestCooldown(TestCooldownException ex) {
        log.warn("Test cooldown active: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ApiError("test_cooldown", ex.getMessage(), ex.availableAt().toString()));
    }
}

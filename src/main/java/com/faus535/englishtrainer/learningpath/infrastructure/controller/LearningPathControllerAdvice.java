package com.faus535.englishtrainer.learningpath.infrastructure.controller;

import com.faus535.englishtrainer.learningpath.domain.error.LearningPathException;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.learningpath")
class LearningPathControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(LearningPathControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(LearningPathNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(LearningPathNotFoundException ex) {
        log.error("Learning path not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", ex.getMessage()));
    }

    @ExceptionHandler(LearningPathException.class)
    ResponseEntity<ApiError> handleLearningPathException(LearningPathException ex) {
        log.error("Learning path error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("learning_path_error", ex.getMessage()));
    }
}

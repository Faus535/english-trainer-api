package com.faus535.englishtrainer.review.infrastructure.controller;

import com.faus535.englishtrainer.review.domain.error.ReviewItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.review")
class ReviewControllerAdvice {

    record ApiError(String code, String message) {}

    @ExceptionHandler(ReviewItemNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(ReviewItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", ex.getMessage()));
    }
}

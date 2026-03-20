package com.faus535.englishtrainer.moduleprogress.infrastructure.controller;

import com.faus535.englishtrainer.moduleprogress.domain.error.ModuleProgressNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.moduleprogress")
class ModuleProgressControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ModuleProgressControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(ModuleProgressNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(ModuleProgressNotFoundException ex) {
        log.error("Module progress not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", "Module progress not found"));
    }
}

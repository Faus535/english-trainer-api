package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.domain.error.ActiveSessionExistsException;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.session")
class SessionControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(SessionControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(SessionNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(SessionNotFoundException ex) {
        log.error("Session not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", "Session not found"));
    }

    @ExceptionHandler(ActiveSessionExistsException.class)
    ResponseEntity<ApiError> handleActiveSession(ActiveSessionExistsException ex) {
        log.error("Active session exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("active_session_exists", "User already has an active session"));
    }
}

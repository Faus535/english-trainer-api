package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.domain.error.AccountUsesGoogleException;
import com.faus535.englishtrainer.auth.domain.error.EmailAlreadyExistsException;
import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.auth")
class AuthControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(AuthControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.error("Invalid credentials: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError("unauthorized", "Invalid email or password"));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    ResponseEntity<ApiError> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.error("Email already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("already_exists", "Email already registered"));
    }

    @ExceptionHandler(GoogleAuthException.class)
    ResponseEntity<ApiError> handleGoogleAuth(GoogleAuthException ex) {
        log.error("Google auth error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError("google_auth_error", ex.getMessage()));
    }

    @ExceptionHandler(AccountUsesGoogleException.class)
    ResponseEntity<ApiError> handleAccountUsesGoogle(AccountUsesGoogleException ex) {
        log.error("Account uses Google: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("account_uses_google", "This account uses Google Sign-In"));
    }
}

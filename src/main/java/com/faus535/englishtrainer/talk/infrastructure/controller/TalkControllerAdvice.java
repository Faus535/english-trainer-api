package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationAlreadyEndedException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import com.faus535.englishtrainer.talk.domain.error.TalkMaxConversationsExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.talk")
class TalkControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(TalkControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(TalkConversationNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(TalkConversationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", ex.getMessage()));
    }

    @ExceptionHandler(TalkConversationAlreadyEndedException.class)
    ResponseEntity<ApiError> handleAlreadyEnded(TalkConversationAlreadyEndedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("conversation_ended", ex.getMessage()));
    }

    @ExceptionHandler(TalkMaxConversationsExceededException.class)
    ResponseEntity<ApiError> handleMaxExceeded(TalkMaxConversationsExceededException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("max_conversations", ex.getMessage()));
    }

    @ExceptionHandler(TalkAiException.class)
    ResponseEntity<ApiError> handleAiError(TalkAiException ex) {
        log.error("Talk AI error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiError("ai_unavailable", "AI service temporarily unavailable"));
    }
}

package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import com.faus535.englishtrainer.conversation.domain.error.MaxConversationsExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.conversation")
class ConversationControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ConversationControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(ConversationNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(ConversationNotFoundException ex) {
        log.error("Conversation not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", "Conversation not found"));
    }

    @ExceptionHandler(ConversationAlreadyEndedException.class)
    ResponseEntity<ApiError> handleAlreadyEnded(ConversationAlreadyEndedException ex) {
        log.error("Conversation already ended: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("conversation_ended", ex.getMessage()));
    }

    @ExceptionHandler(MaxConversationsExceededException.class)
    ResponseEntity<ApiError> handleMaxConversations(MaxConversationsExceededException ex) {
        log.error("Max conversations exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ApiError("max_conversations", ex.getMessage()));
    }

    @ExceptionHandler(AiTutorException.class)
    ResponseEntity<ApiError> handleAiTutor(AiTutorException ex) {
        log.error("AI tutor error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiError("ai_unavailable", "AI tutor service is temporarily unavailable"));
    }
}

package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.domain.error.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ArticleControllerAdvice {

    record ApiError(String message) {}

    @ExceptionHandler(ArticleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError handleNotFound(ArticleNotFoundException ex) {
        return new ApiError(ex.getMessage());
    }

    @ExceptionHandler(ArticleAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ApiError handleAccessDenied(ArticleAccessDeniedException ex) {
        return new ApiError(ex.getMessage());
    }

    @ExceptionHandler(ArticleAiException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    ApiError handleAiError(ArticleAiException ex) {
        return new ApiError("AI service unavailable");
    }

    @ExceptionHandler(ArticleAlreadyCompletedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiError handleAlreadyCompleted(ArticleAlreadyCompletedException ex) {
        return new ApiError(ex.getMessage());
    }

    @ExceptionHandler(DuplicateMarkedWordException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiError handleDuplicateWord(DuplicateMarkedWordException ex) {
        return new ApiError(ex.getMessage());
    }

    @ExceptionHandler(ArticleQuestionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError handleQuestionNotFound(ArticleQuestionNotFoundException ex) {
        return new ApiError(ex.getMessage());
    }

    @ExceptionHandler(AnswerTooShortException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ApiError handleAnswerTooShort(AnswerTooShortException ex) {
        return new ApiError(ex.getMessage());
    }

    @ExceptionHandler(QuestionAlreadyAnsweredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiError handleQuestionAlreadyAnswered(QuestionAlreadyAnsweredException ex) {
        return new ApiError(ex.getMessage());
    }
}

package com.faus535.englishtrainer.tutorerror.infrastructure.controller;

import com.faus535.englishtrainer.tutorerror.domain.TutorError;

import java.time.Instant;
import java.util.UUID;

record TutorErrorResponse(
        UUID id,
        UUID userId,
        String errorType,
        String originalText,
        String correctedText,
        String rule,
        int occurrenceCount,
        Instant firstSeen,
        Instant lastSeen
) {
    static TutorErrorResponse from(TutorError error) {
        return new TutorErrorResponse(
                error.id().value(),
                error.userId().value(),
                error.errorType(),
                error.originalText(),
                error.correctedText(),
                error.rule(),
                error.occurrenceCount(),
                error.firstSeen(),
                error.lastSeen());
    }
}

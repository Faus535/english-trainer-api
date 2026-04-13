package com.faus535.englishtrainer.pronunciation.domain;

import java.time.Instant;
import java.util.UUID;

public record MiniConversationTurn(
        UUID id,
        int turnNumber,
        String targetPhrase,
        String recognizedText,
        int score,
        Instant evaluatedAt) {}

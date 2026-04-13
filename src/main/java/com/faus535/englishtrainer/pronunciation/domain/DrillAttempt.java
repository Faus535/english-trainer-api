package com.faus535.englishtrainer.pronunciation.domain;

import java.time.Instant;
import java.util.UUID;

public record DrillAttempt(UUID id, UUID userId, int score, String recognizedText, Instant attemptedAt) {}

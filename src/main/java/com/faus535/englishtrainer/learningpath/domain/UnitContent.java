package com.faus535.englishtrainer.learningpath.domain;

import java.time.Instant;
import java.util.UUID;

public record UnitContent(ContentType contentType, UUID contentId, boolean practiced, Instant lastPracticedAt) {
}

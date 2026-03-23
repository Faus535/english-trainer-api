package com.faus535.englishtrainer.errorpattern.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ErrorPatternRepository {
    ErrorPattern save(ErrorPattern errorPattern);
    Optional<ErrorPattern> findByUserAndCategoryAndPattern(UUID userId, ErrorCategory category, String pattern);
    List<ErrorPattern> findByUserId(UUID userId);
    List<ErrorPattern> findByUserIdOrderByOccurrenceCountDesc(UUID userId);
}

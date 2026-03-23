package com.faus535.englishtrainer.errorpattern.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaErrorPatternRepository extends JpaRepository<ErrorPatternEntity, UUID> {
    Optional<ErrorPatternEntity> findByUserIdAndCategoryAndPattern(UUID userId, String category, String pattern);
    List<ErrorPatternEntity> findByUserId(UUID userId);
    List<ErrorPatternEntity> findByUserIdOrderByOccurrenceCountDesc(UUID userId);
}

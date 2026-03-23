package com.faus535.englishtrainer.errorpattern.infrastructure.persistence;

import com.faus535.englishtrainer.errorpattern.domain.ErrorCategory;
import com.faus535.englishtrainer.errorpattern.domain.ErrorPattern;
import com.faus535.englishtrainer.errorpattern.domain.ErrorPatternRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaErrorPatternRepositoryAdapter implements ErrorPatternRepository {

    private final JpaErrorPatternRepository jpaRepository;

    JpaErrorPatternRepositoryAdapter(JpaErrorPatternRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ErrorPattern save(ErrorPattern errorPattern) {
        Optional<ErrorPatternEntity> existing = jpaRepository.findById(errorPattern.id().value());
        if (existing.isPresent()) {
            ErrorPatternEntity entity = existing.get();
            entity.updateFrom(errorPattern);
            return jpaRepository.save(entity).toAggregate();
        }
        return jpaRepository.save(ErrorPatternEntity.fromAggregate(errorPattern)).toAggregate();
    }

    @Override
    public Optional<ErrorPattern> findByUserAndCategoryAndPattern(UUID userId, ErrorCategory category, String pattern) {
        return jpaRepository.findByUserIdAndCategoryAndPattern(userId, category.name(), pattern)
                .map(ErrorPatternEntity::toAggregate);
    }

    @Override
    public List<ErrorPattern> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(ErrorPatternEntity::toAggregate).toList();
    }

    @Override
    public List<ErrorPattern> findByUserIdOrderByOccurrenceCountDesc(UUID userId) {
        return jpaRepository.findByUserIdOrderByOccurrenceCountDesc(userId).stream()
                .map(ErrorPatternEntity::toAggregate).toList();
    }
}

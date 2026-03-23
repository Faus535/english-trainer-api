package com.faus535.englishtrainer.errorpattern.application;

import com.faus535.englishtrainer.errorpattern.domain.ErrorCategory;
import com.faus535.englishtrainer.errorpattern.domain.ErrorPattern;
import com.faus535.englishtrainer.errorpattern.domain.ErrorPatternRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@UseCase
public class RecordErrorPatternUseCase {

    private final ErrorPatternRepository repository;

    public RecordErrorPatternUseCase(ErrorPatternRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ErrorPattern execute(UUID userId, ErrorCategory category, String pattern, String example) {
        Optional<ErrorPattern> existing = repository.findByUserAndCategoryAndPattern(userId, category, pattern);
        if (existing.isPresent()) {
            ErrorPattern updated = existing.get().recordOccurrence(example);
            return repository.save(updated);
        }
        ErrorPattern newPattern = ErrorPattern.create(userId, category, pattern, example);
        return repository.save(newPattern);
    }
}

package com.faus535.englishtrainer.errorpattern.application;

import com.faus535.englishtrainer.errorpattern.domain.ErrorPattern;
import com.faus535.englishtrainer.errorpattern.domain.ErrorPatternRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetErrorPatternsUseCase {

    private final ErrorPatternRepository repository;

    public GetErrorPatternsUseCase(ErrorPatternRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ErrorPattern> execute(UUID userId) {
        return repository.findByUserIdOrderByOccurrenceCountDesc(userId);
    }
}

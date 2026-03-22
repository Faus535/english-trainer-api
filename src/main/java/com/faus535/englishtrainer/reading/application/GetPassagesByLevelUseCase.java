package com.faus535.englishtrainer.reading.application;

import com.faus535.englishtrainer.reading.domain.ReadingPassage;
import com.faus535.englishtrainer.reading.domain.ReadingPassageRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetPassagesByLevelUseCase {

    private final ReadingPassageRepository repository;

    public GetPassagesByLevelUseCase(ReadingPassageRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ReadingPassage> execute(String level) {
        return repository.findByLevel(level.toLowerCase());
    }
}

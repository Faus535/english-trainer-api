package com.faus535.englishtrainer.reading.application;

import com.faus535.englishtrainer.reading.domain.ReadingPassage;
import com.faus535.englishtrainer.reading.domain.ReadingPassageId;
import com.faus535.englishtrainer.reading.domain.ReadingPassageRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@UseCase
public class GetPassageByIdUseCase {

    private final ReadingPassageRepository repository;

    public GetPassageByIdUseCase(ReadingPassageRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<ReadingPassage> execute(UUID id) {
        return repository.findById(new ReadingPassageId(id));
    }
}

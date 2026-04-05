package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentMother;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetSuggestedImmerseContentUseCaseTest {

    private InMemoryImmerseContentRepository repository;
    private GetSuggestedImmerseContentUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryImmerseContentRepository();
        useCase = new GetSuggestedImmerseContentUseCase(repository);
    }

    @Test
    void shouldReturnLatestContentForUser() {
        UUID userId = UUID.randomUUID();
        repository.save(ImmerseContentMother.withUserId(userId));
        repository.save(ImmerseContentMother.withUserId(userId));

        Optional<ImmerseContent> result = useCase.execute(userId);

        assertTrue(result.isPresent());
    }

    @Test
    void shouldReturnEmptyOptionalForUnknownUser() {
        UUID unknownUserId = UUID.randomUUID();

        Optional<ImmerseContent> result = useCase.execute(unknownUserId);

        assertTrue(result.isEmpty());
    }
}

package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentMother;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetImmerseHistoryUseCaseTest {

    private InMemoryImmerseContentRepository repository;
    private GetImmerseHistoryUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryImmerseContentRepository();
        useCase = new GetImmerseHistoryUseCase(repository);
    }

    @Test
    void shouldReturnPagedHistory() {
        UUID userId = UUID.randomUUID();
        repository.save(ImmerseContentMother.withUserId(userId));
        repository.save(ImmerseContentMother.withUserId(userId));
        repository.save(ImmerseContentMother.withUserId(userId));

        List<ImmerseContent> result = useCase.execute(userId, 0, 2);

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListForUnknownUser() {
        UUID unknownUserId = UUID.randomUUID();

        List<ImmerseContent> result = useCase.execute(unknownUserId, 0, 10);

        assertTrue(result.isEmpty());
    }
}

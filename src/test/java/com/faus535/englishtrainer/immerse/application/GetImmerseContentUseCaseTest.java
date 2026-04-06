package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentMother;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentAccessDeniedException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetImmerseContentUseCaseTest {

    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private InMemoryImmerseContentRepository repository;
    private GetImmerseContentUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryImmerseContentRepository();
        useCase = new GetImmerseContentUseCase(repository);
    }

    @Test
    void shouldReturnContentWhenFoundAndOwnedByUser() throws Exception {
        ImmerseContent content = ImmerseContentMother.processed();
        repository.save(content);

        ImmerseContent result = useCase.execute(content.id().value(), DEFAULT_USER_ID);

        assertEquals(content.id(), result.id());
        assertEquals(content.title(), result.title());
    }

    @Test
    void shouldThrowAccessDeniedForWrongUser() {
        ImmerseContent content = ImmerseContentMother.processed();
        repository.save(content);

        UUID wrongUserId = UUID.randomUUID();
        assertThrows(ImmerseContentAccessDeniedException.class,
                () -> useCase.execute(content.id().value(), wrongUserId));
    }

    @Test
    void shouldThrowImmerseContentNotFoundExceptionWhenMissing() {
        UUID randomId = UUID.randomUUID();
        assertThrows(ImmerseContentNotFoundException.class,
                () -> useCase.execute(randomId, DEFAULT_USER_ID));
    }
}

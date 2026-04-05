package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotProcessedException;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseExerciseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetImmerseExercisesUseCaseTest {

    private InMemoryImmerseContentRepository contentRepository;
    private InMemoryImmerseExerciseRepository exerciseRepository;
    private GetImmerseExercisesUseCase useCase;

    @BeforeEach
    void setUp() {
        contentRepository = new InMemoryImmerseContentRepository();
        exerciseRepository = new InMemoryImmerseExerciseRepository();
        useCase = new GetImmerseExercisesUseCase(contentRepository, exerciseRepository);
    }

    @Test
    void shouldReturnExercisesForProcessedContent() throws Exception {
        ImmerseContent content = ImmerseContentMother.processed();
        contentRepository.save(content);

        List<ImmerseExercise> exercises = List.of(
                ImmerseExerciseMother.multipleChoice(content.id()),
                ImmerseExerciseMother.fillTheGap(content.id())
        );
        exerciseRepository.saveAll(exercises);

        List<ImmerseExercise> result = useCase.execute(content.id().value());

        assertEquals(2, result.size());
    }

    @Test
    void shouldThrowImmerseContentNotProcessedExceptionWhenPending() {
        ImmerseContent content = ImmerseContentMother.pending();
        contentRepository.save(content);

        assertThrows(ImmerseContentNotProcessedException.class,
                () -> useCase.execute(content.id().value()));
    }

    @Test
    void shouldThrowImmerseContentNotFoundExceptionWhenMissing() {
        UUID randomId = UUID.randomUUID();
        assertThrows(ImmerseContentNotFoundException.class,
                () -> useCase.execute(randomId));
    }
}

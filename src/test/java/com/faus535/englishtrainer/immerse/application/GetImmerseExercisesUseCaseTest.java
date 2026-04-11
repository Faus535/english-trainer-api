package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentAccessDeniedException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotProcessedException;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseExerciseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GetImmerseExercisesUseCaseTest {

    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

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

        List<ImmerseExercise> result = useCase.execute(content.id().value(), DEFAULT_USER_ID, ExerciseTypeFilter.ALL);

        assertEquals(2, result.size());
    }

    @Test
    void shouldThrowAccessDeniedForWrongUser() {
        ImmerseContent content = ImmerseContentMother.processed();
        contentRepository.save(content);

        UUID wrongUserId = UUID.randomUUID();
        assertThrows(ImmerseContentAccessDeniedException.class,
                () -> useCase.execute(content.id().value(), wrongUserId, ExerciseTypeFilter.ALL));
    }

    @Test
    void shouldThrowImmerseContentNotProcessedExceptionWhenPending() {
        ImmerseContent content = ImmerseContentMother.pending();
        contentRepository.save(content);

        assertThrows(ImmerseContentNotProcessedException.class,
                () -> useCase.execute(content.id().value(), DEFAULT_USER_ID, ExerciseTypeFilter.ALL));
    }

    @Test
    void shouldThrowImmerseContentNotFoundExceptionWhenMissing() {
        UUID randomId = UUID.randomUUID();
        assertThrows(ImmerseContentNotFoundException.class,
                () -> useCase.execute(randomId, DEFAULT_USER_ID, ExerciseTypeFilter.ALL));
    }

    @Test
    void execute_returnsAllExercises_whenFilterIsAll() throws Exception {
        ImmerseContent content = ImmerseContentMother.processed();
        contentRepository.save(content);

        exerciseRepository.saveAll(List.of(
                ImmerseExerciseMother.multipleChoice(content.id()),
                ImmerseExerciseMother.listeningCloze(content.id())
        ));

        List<ImmerseExercise> result = useCase.execute(content.id().value(), DEFAULT_USER_ID, ExerciseTypeFilter.ALL);

        assertEquals(2, result.size());
    }

    @Test
    void execute_returnsOnlyListeningCloze_whenFilterIsListeningCloze() throws Exception {
        ImmerseContent content = ImmerseContentMother.processed();
        contentRepository.save(content);

        exerciseRepository.saveAll(List.of(
                ImmerseExerciseMother.multipleChoice(content.id()),
                ImmerseExerciseMother.listeningCloze(content.id())
        ));

        List<ImmerseExercise> result = useCase.execute(content.id().value(), DEFAULT_USER_ID, ExerciseTypeFilter.LISTENING_CLOZE);

        assertEquals(1, result.size());
        assertEquals(ExerciseType.LISTENING_CLOZE, result.get(0).exerciseType());
    }

    @Test
    void execute_returnsOnlyRegular_whenFilterIsRegular() throws Exception {
        ImmerseContent content = ImmerseContentMother.processed();
        contentRepository.save(content);

        exerciseRepository.saveAll(List.of(
                ImmerseExerciseMother.multipleChoice(content.id()),
                ImmerseExerciseMother.listeningCloze(content.id())
        ));

        List<ImmerseExercise> result = useCase.execute(content.id().value(), DEFAULT_USER_ID, ExerciseTypeFilter.REGULAR);

        assertEquals(1, result.size());
        assertNotEquals(ExerciseType.LISTENING_CLOZE, result.get(0).exerciseType());
    }
}

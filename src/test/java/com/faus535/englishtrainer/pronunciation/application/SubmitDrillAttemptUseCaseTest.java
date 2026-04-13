package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrill;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillMother;
import com.faus535.englishtrainer.pronunciation.domain.event.PronunciationDrillCompletedEvent;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationDrillNotFoundException;
import com.faus535.englishtrainer.pronunciation.infrastructure.InMemoryPronunciationDrillRepository;
import com.faus535.englishtrainer.pronunciation.infrastructure.StubPronunciationAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubmitDrillAttemptUseCaseTest {

    private InMemoryPronunciationDrillRepository drillRepository;
    private StubPronunciationAiPort aiPort;
    private ApplicationEventPublisher eventPublisher;
    private SubmitDrillAttemptUseCase useCase;

    @BeforeEach
    void setUp() {
        drillRepository = new InMemoryPronunciationDrillRepository();
        aiPort = new StubPronunciationAiPort();
        eventPublisher = mock(ApplicationEventPublisher.class);
        useCase = new SubmitDrillAttemptUseCase(drillRepository, aiPort, eventPublisher);
    }

    @Test
    void shouldReturnScoreAndFeedbackWhenDrillExists() throws Exception {
        PronunciationDrill drill = PronunciationDrillMother.withThFocus();
        drillRepository.add(drill);
        UUID userId = UUID.randomUUID();

        DrillAttemptResultDto result = useCase.execute(
                drill.id().value(), userId, "I tought about the teory", 0.7);

        assertNotNull(result);
        assertTrue(result.score() >= 0 && result.score() <= 100);
        assertNotNull(result.feedback());
    }

    @Test
    void shouldThrowNotFoundWhenDrillDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        assertThrows(PronunciationDrillNotFoundException.class,
                () -> useCase.execute(nonExistentId, userId, "recognized", 0.8));
    }

    @Test
    void shouldFireDrillCompletedEventWhenScoreAboveThreshold() throws Exception {
        PronunciationDrill drill = PronunciationDrillMother.withThFocus();
        drillRepository.add(drill);
        UUID userId = UUID.randomUUID();
        aiPort.setDrillFeedbackToReturn(
                new com.faus535.englishtrainer.pronunciation.domain.PronunciationAiPort.DrillFeedbackResult(
                        80, "Good job!"));

        useCase.execute(drill.id().value(), userId, "I thought about the theory", 0.85);

        verify(eventPublisher, times(1)).publishEvent(any(PronunciationDrillCompletedEvent.class));
    }

    @Test
    void shouldNotFireEventWhenScoreBelowThreshold() throws Exception {
        PronunciationDrill drill = PronunciationDrillMother.withThFocus();
        drillRepository.add(drill);
        UUID userId = UUID.randomUUID();
        aiPort.setDrillFeedbackToReturn(
                new com.faus535.englishtrainer.pronunciation.domain.PronunciationAiPort.DrillFeedbackResult(
                        50, "Needs improvement"));

        useCase.execute(drill.id().value(), userId, "I tought", 0.5);

        verify(eventPublisher, never()).publishEvent(any(PronunciationDrillCompletedEvent.class));
    }

    @Test
    void shouldCalculatePerfectStreakCorrectly() throws Exception {
        UUID userId = UUID.randomUUID();
        PronunciationDrill drill = PronunciationDrillMother.withUserPerfectStreak(userId, 2);
        drillRepository.add(drill);
        aiPort.setDrillFeedbackToReturn(
                new com.faus535.englishtrainer.pronunciation.domain.PronunciationAiPort.DrillFeedbackResult(
                        95, "Perfect!"));

        DrillAttemptResultDto result = useCase.execute(
                drill.id().value(), userId, "I thought about the theory", 0.98);

        assertEquals(3, result.perfectStreak());
    }
}

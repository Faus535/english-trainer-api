package com.faus535.englishtrainer.vocabulary.infrastructure.event;

import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;
import com.faus535.englishtrainer.spacedrepetition.domain.event.ReviewCompletedEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabMastery;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryMother;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryStatus;
import com.faus535.englishtrainer.vocabulary.domain.event.WordMasteredEvent;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabMasteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class SrsGraduationListenerTest {

    private InMemoryVocabMasteryRepository vocabMasteryRepository;
    private ApplicationEventPublisher eventPublisher;
    private SrsGraduationListener listener;

    @BeforeEach
    void setUp() {
        vocabMasteryRepository = new InMemoryVocabMasteryRepository();
        eventPublisher = mock(ApplicationEventPublisher.class);
        listener = new SrsGraduationListener(vocabMasteryRepository, eventPublisher);
    }

    @Test
    void should_graduate_vocab_mastery_to_mastered_when_srs_graduates() {
        UserProfileId userId = UserProfileId.generate();
        VocabMastery mastery = VocabMasteryMother.create(userId, "hello");
        vocabMasteryRepository.save(mastery);

        ReviewCompletedEvent event = new ReviewCompletedEvent(
                SpacedRepetitionItemId.generate(), userId, true, "vocabulary-word", "vocab-hello");

        listener.handleReviewCompleted(event);

        VocabMastery updated = vocabMasteryRepository.findByUserIdAndWord(userId, "hello").orElseThrow();
        assertEquals(VocabMasteryStatus.MASTERED, updated.status());

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, atLeastOnce()).publishEvent(captor.capture());
        assertTrue(captor.getAllValues().stream().anyMatch(e -> e instanceof WordMasteredEvent));
    }

    @Test
    void should_ignore_non_vocabulary_items() {
        UserProfileId userId = UserProfileId.generate();
        VocabMastery mastery = VocabMasteryMother.create(userId, "hello");
        vocabMasteryRepository.save(mastery);

        ReviewCompletedEvent event = new ReviewCompletedEvent(
                SpacedRepetitionItemId.generate(), userId, true, "module-unit", "vocabulary-A1-0");

        listener.handleReviewCompleted(event);

        VocabMastery unchanged = vocabMasteryRepository.findByUserIdAndWord(userId, "hello").orElseThrow();
        assertEquals(VocabMasteryStatus.LEARNING, unchanged.status());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void should_ignore_non_graduated_reviews() {
        UserProfileId userId = UserProfileId.generate();
        VocabMastery mastery = VocabMasteryMother.create(userId, "hello");
        vocabMasteryRepository.save(mastery);

        ReviewCompletedEvent event = new ReviewCompletedEvent(
                SpacedRepetitionItemId.generate(), userId, false, "vocabulary-word", "vocab-hello");

        listener.handleReviewCompleted(event);

        VocabMastery unchanged = vocabMasteryRepository.findByUserIdAndWord(userId, "hello").orElseThrow();
        assertEquals(VocabMasteryStatus.LEARNING, unchanged.status());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void should_handle_backward_compatible_event() {
        UserProfileId userId = UserProfileId.generate();

        ReviewCompletedEvent event = new ReviewCompletedEvent(
                SpacedRepetitionItemId.generate(), userId);

        listener.handleReviewCompleted(event);

        assertFalse(event.graduated());
        assertEquals("module-unit", event.itemType());
        assertNull(event.unitReference());
        verifyNoInteractions(eventPublisher);
    }
}

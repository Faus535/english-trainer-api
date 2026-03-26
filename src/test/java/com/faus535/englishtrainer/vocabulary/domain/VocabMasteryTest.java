package com.faus535.englishtrainer.vocabulary.domain;

import com.faus535.englishtrainer.vocabulary.domain.event.WordLearnedEvent;
import com.faus535.englishtrainer.vocabulary.domain.event.WordMasteredEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VocabMasteryTest {

    @Test
    void should_create_with_learning_status() {
        VocabMastery mastery = VocabMasteryMother.create();

        assertEquals(VocabMasteryStatus.LEARNING, mastery.status());
        assertEquals(0, mastery.correctCount());
        assertEquals(0, mastery.incorrectCount());
        assertEquals(0, mastery.totalAttempts());
        assertNull(mastery.learnedAt());
        assertNotNull(mastery.createdAt());
    }

    @Test
    void should_increment_correct_count() {
        VocabMastery mastery = VocabMasteryMother.create();

        VocabMastery updated = mastery.recordCorrectAnswer();

        assertEquals(1, updated.correctCount());
        assertEquals(0, updated.incorrectCount());
        assertEquals(1, updated.totalAttempts());
        assertEquals(VocabMasteryStatus.LEARNING, updated.status());
    }

    @Test
    void should_increment_incorrect_count() {
        VocabMastery mastery = VocabMasteryMother.create();

        VocabMastery updated = mastery.recordIncorrectAnswer();

        assertEquals(0, updated.correctCount());
        assertEquals(1, updated.incorrectCount());
        assertEquals(1, updated.totalAttempts());
        assertEquals(VocabMasteryStatus.LEARNING, updated.status());
    }

    @Test
    void should_transition_to_learned_after_3_correct_with_70_percent_accuracy() {
        VocabMastery mastery = VocabMasteryMother.create();

        mastery = mastery.recordCorrectAnswer();
        mastery = mastery.recordCorrectAnswer();
        VocabMastery learned = mastery.recordCorrectAnswer();

        assertEquals(VocabMasteryStatus.LEARNED, learned.status());
        assertNotNull(learned.learnedAt());
    }

    @Test
    void should_publish_word_learned_event_on_transition() {
        VocabMastery mastery = VocabMasteryMother.create();

        mastery = mastery.recordCorrectAnswer();
        mastery = mastery.recordCorrectAnswer();
        VocabMastery learned = mastery.recordCorrectAnswer();

        var events = learned.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(WordLearnedEvent.class, events.get(0));
    }

    @Test
    void should_not_transition_to_learned_with_low_accuracy() {
        VocabMastery mastery = VocabMasteryMother.create();

        // 3 correct, 5 incorrect = 37.5% accuracy
        mastery = mastery.recordCorrectAnswer();
        mastery = mastery.recordIncorrectAnswer();
        mastery = mastery.recordIncorrectAnswer();
        mastery = mastery.recordIncorrectAnswer();
        mastery = mastery.recordIncorrectAnswer();
        mastery = mastery.recordIncorrectAnswer();
        mastery = mastery.recordCorrectAnswer();
        mastery = mastery.recordCorrectAnswer();

        assertEquals(VocabMasteryStatus.LEARNING, mastery.status());
    }

    @Test
    void should_regress_from_learned_to_learning_when_accuracy_drops_below_60() {
        VocabMastery mastery = VocabMasteryMother.create();

        // Get to LEARNED: 3 correct, 0 incorrect = 100%
        mastery = mastery.recordCorrectAnswer();
        mastery = mastery.recordCorrectAnswer();
        mastery = mastery.recordCorrectAnswer();
        assertEquals(VocabMasteryStatus.LEARNED, mastery.status());

        // Add incorrect answers to drop below 60%: 3 correct, 5 incorrect = 37.5%
        mastery = mastery.recordIncorrectAnswer();
        mastery = mastery.recordIncorrectAnswer();
        mastery = mastery.recordIncorrectAnswer();
        mastery = mastery.recordIncorrectAnswer();
        mastery = mastery.recordIncorrectAnswer();

        assertEquals(VocabMasteryStatus.LEARNING, mastery.status());
    }

    @Test
    void should_graduate_to_mastered() {
        VocabMastery mastery = VocabMasteryMother.create();

        VocabMastery mastered = mastery.graduate();

        assertEquals(VocabMasteryStatus.MASTERED, mastered.status());
        assertNotNull(mastered.learnedAt());
    }

    @Test
    void should_publish_word_mastered_event_on_graduation() {
        VocabMastery mastery = VocabMasteryMother.create();

        VocabMastery mastered = mastery.graduate();

        var events = mastered.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(WordMasteredEvent.class, events.get(0));
    }

    @Test
    void mastered_status_should_be_terminal() {
        VocabMastery mastery = VocabMasteryMother.create();
        VocabMastery mastered = mastery.graduate();

        // Even with incorrect answers, MASTERED stays MASTERED
        VocabMastery afterIncorrect = mastered.recordIncorrectAnswer();

        assertEquals(VocabMasteryStatus.MASTERED, afterIncorrect.status());
    }

    @Test
    void should_calculate_accuracy() {
        VocabMastery mastery = VocabMasteryMother.create();

        assertEquals(0.0, mastery.accuracy());

        mastery = mastery.recordCorrectAnswer();
        mastery = mastery.recordCorrectAnswer();
        mastery = mastery.recordIncorrectAnswer();

        assertEquals(66.66, mastery.accuracy(), 0.67);
    }

    @Test
    void should_return_new_instances_on_mutation() {
        VocabMastery original = VocabMasteryMother.create();

        VocabMastery afterCorrect = original.recordCorrectAnswer();
        VocabMastery afterIncorrect = original.recordIncorrectAnswer();
        VocabMastery afterGraduate = original.graduate();

        assertNotSame(original, afterCorrect);
        assertNotSame(original, afterIncorrect);
        assertNotSame(original, afterGraduate);

        // Original should be unchanged
        assertEquals(0, original.correctCount());
        assertEquals(0, original.incorrectCount());
        assertEquals(VocabMasteryStatus.LEARNING, original.status());
    }

    @Test
    void should_allow_null_vocab_entry_id() {
        VocabMastery mastery = VocabMasteryMother.withoutVocabEntry();

        assertNull(mastery.vocabEntryId());
        assertEquals("ephemeral", mastery.word());
    }
}

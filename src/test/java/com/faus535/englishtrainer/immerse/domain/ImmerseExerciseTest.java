package com.faus535.englishtrainer.immerse.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImmerseExerciseTest {

    private static final ImmerseContentId CONTENT_ID = new ImmerseContentId(java.util.UUID.randomUUID());

    @Test
    void listeningCloze_hasListenTextAndBlankPosition() {
        ImmerseExercise exercise = ImmerseExerciseMother.listeningCloze(CONTENT_ID);

        assertEquals(ExerciseType.LISTENING_CLOZE, exercise.exerciseType());
        assertEquals("She ordered a cup of coffee at the café.", exercise.listenText());
        assertEquals(5, exercise.blankPosition());
        assertEquals("coffee", exercise.correctAnswer());
    }

    @Test
    void regularExercise_hasNullListenText() {
        ImmerseExercise exercise = ImmerseExerciseMother.multipleChoice(CONTENT_ID);

        assertNull(exercise.listenText());
        assertNull(exercise.blankPosition());
    }
}

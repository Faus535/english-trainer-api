package com.faus535.englishtrainer.article.domain;

import com.faus535.englishtrainer.article.domain.error.AnswerTooShortException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArticleQuestionAnswerTest {

    private static final ArticleQuestionId QUESTION_ID = ArticleQuestionId.generate();

    private static String wordsOfLength(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(" ");
            sb.append("word");
        }
        return sb.toString();
    }

    @Test
    void shouldPassValidationWhenB1LevelAndExactly20Words() throws AnswerTooShortException {
        String answer = wordsOfLength(20);
        ArticleQuestionAnswer result = ArticleQuestionAnswer.create(QUESTION_ID, answer,
                ArticleLevel.B1.minWords(), true, "Good.", "Clear.", "Well done.");
        assertNotNull(result);
        assertEquals(answer, result.userAnswer());
    }

    @Test
    void shouldThrowWhenB1LevelAnd19Words() {
        String shortAnswer = wordsOfLength(19);
        assertThrows(AnswerTooShortException.class, () ->
                ArticleQuestionAnswer.create(QUESTION_ID, shortAnswer,
                        ArticleLevel.B1.minWords(), true, "Good.", "Clear.", "Done."));
    }

    @Test
    void shouldPassValidationWhenB2LevelAndExactly30Words() throws AnswerTooShortException {
        String answer = wordsOfLength(30);
        ArticleQuestionAnswer result = ArticleQuestionAnswer.create(QUESTION_ID, answer,
                ArticleLevel.B2.minWords(), true, "Good.", "Clear.", "Well done.");
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenB2LevelAnd29Words() {
        String shortAnswer = wordsOfLength(29);
        assertThrows(AnswerTooShortException.class, () ->
                ArticleQuestionAnswer.create(QUESTION_ID, shortAnswer,
                        ArticleLevel.B2.minWords(), true, "Good.", "Clear.", "Done."));
    }

    @Test
    void shouldPassValidationWhenC1LevelAndExactly40Words() throws AnswerTooShortException {
        String answer = wordsOfLength(40);
        ArticleQuestionAnswer result = ArticleQuestionAnswer.create(QUESTION_ID, answer,
                ArticleLevel.C1.minWords(), true, "Good.", "Clear.", "Well done.");
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenC1LevelAnd39Words() {
        String shortAnswer = wordsOfLength(39);
        assertThrows(AnswerTooShortException.class, () ->
                ArticleQuestionAnswer.create(QUESTION_ID, shortAnswer,
                        ArticleLevel.C1.minWords(), true, "Good.", "Clear.", "Done."));
    }

    @Test
    void shouldPassValidationWhenMoreThanMinWords() throws AnswerTooShortException {
        String longAnswer = wordsOfLength(50);
        ArticleQuestionAnswer result = ArticleQuestionAnswer.create(QUESTION_ID, longAnswer,
                ArticleLevel.B1.minWords(), true, "Good.", "Clear.", "Well done.");
        assertNotNull(result);
    }
}

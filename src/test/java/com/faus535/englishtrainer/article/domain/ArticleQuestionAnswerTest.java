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
    void exactly40WordsPassesValidation() throws AnswerTooShortException {
        String answer = wordsOfLength(40);
        ArticleQuestionAnswer result = ArticleQuestionAnswer.create(QUESTION_ID, answer, true,
                "Good.", "Clear.", "Well done.");
        assertNotNull(result);
        assertEquals(answer, result.userAnswer());
    }

    @Test
    void thirtyNineWordsThrowsAnswerTooShortException() {
        String shortAnswer = wordsOfLength(39);
        assertThrows(AnswerTooShortException.class, () ->
                ArticleQuestionAnswer.create(QUESTION_ID, shortAnswer, true, "Good.", "Clear.", "Done."));
    }

    @Test
    void moreThan40WordsPassesValidation() throws AnswerTooShortException {
        String longAnswer = wordsOfLength(50);
        ArticleQuestionAnswer result = ArticleQuestionAnswer.create(QUESTION_ID, longAnswer, true,
                "Good.", "Clear.", "Well done.");
        assertNotNull(result);
    }
}

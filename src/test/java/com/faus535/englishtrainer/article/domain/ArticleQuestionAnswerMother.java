package com.faus535.englishtrainer.article.domain;

import com.faus535.englishtrainer.article.domain.error.AnswerTooShortException;

public final class ArticleQuestionAnswerMother {

    private static final String VALID_ANSWER = "The main argument is that the timeline for the new policies "
            + "is too aggressive according to critics who argue that industry cannot adapt quickly enough "
            + "to meet the new standards without significant economic disruption, widespread job losses, "
            + "and severe long-term damage to global competitiveness.";

    private static final String TOO_SHORT_ANSWER = "The policy is too aggressive.";

    public static ArticleQuestionAnswer valid(ArticleQuestionId questionId) throws AnswerTooShortException {
        return ArticleQuestionAnswer.create(questionId, VALID_ANSWER, 20,
                true, "Good grammar.", "Clear structure.", "Well argued.");
    }

    public static ArticleQuestionAnswer withGrading(ArticleQuestionId questionId) throws AnswerTooShortException {
        return ArticleQuestionAnswer.create(questionId, VALID_ANSWER, 20,
                false, "Minor grammar issues.", "Could improve style.", "Needs more detail.");
    }

    public static String tooShortAnswer() {
        return TOO_SHORT_ANSWER;
    }

    public static String validAnswer() {
        return VALID_ANSWER;
    }
}

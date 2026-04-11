package com.faus535.englishtrainer.article.domain;

import com.faus535.englishtrainer.article.domain.error.AnswerTooShortException;

import java.time.Instant;

public final class ArticleQuestionAnswer {

    private final ArticleQuestionAnswerId id;
    private final ArticleQuestionId questionId;
    private final String userAnswer;
    private final boolean isContentCorrect;
    private final String grammarFeedback;
    private final String styleFeedback;
    private final String correctionSummary;
    private final Instant createdAt;

    private ArticleQuestionAnswer(ArticleQuestionAnswerId id, ArticleQuestionId questionId,
                                   String userAnswer, boolean isContentCorrect, String grammarFeedback,
                                   String styleFeedback, String correctionSummary, Instant createdAt) {
        this.id = id;
        this.questionId = questionId;
        this.userAnswer = userAnswer;
        this.isContentCorrect = isContentCorrect;
        this.grammarFeedback = grammarFeedback;
        this.styleFeedback = styleFeedback;
        this.correctionSummary = correctionSummary;
        this.createdAt = createdAt;
    }

    public static ArticleQuestionAnswer create(ArticleQuestionId questionId, String userAnswer,
                                                int minWords, boolean isContentCorrect, String grammarFeedback,
                                                String styleFeedback, String correctionSummary)
            throws AnswerTooShortException {
        int wordCount = wordCount(userAnswer);
        if (wordCount < minWords) {
            throw new AnswerTooShortException(wordCount, minWords);
        }
        return new ArticleQuestionAnswer(ArticleQuestionAnswerId.generate(), questionId, userAnswer,
                isContentCorrect, grammarFeedback, styleFeedback, correctionSummary, Instant.now());
    }

    public static ArticleQuestionAnswer reconstitute(ArticleQuestionAnswerId id, ArticleQuestionId questionId,
                                                      String userAnswer, boolean isContentCorrect,
                                                      String grammarFeedback, String styleFeedback,
                                                      String correctionSummary, Instant createdAt) {
        return new ArticleQuestionAnswer(id, questionId, userAnswer, isContentCorrect,
                grammarFeedback, styleFeedback, correctionSummary, createdAt);
    }

    static int wordCount(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }

    public ArticleQuestionAnswerId id() { return id; }
    public ArticleQuestionId questionId() { return questionId; }
    public String userAnswer() { return userAnswer; }
    public boolean isContentCorrect() { return isContentCorrect; }
    public String grammarFeedback() { return grammarFeedback; }
    public String styleFeedback() { return styleFeedback; }
    public String correctionSummary() { return correctionSummary; }
    public Instant createdAt() { return createdAt; }
}

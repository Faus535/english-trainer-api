package com.faus535.englishtrainer.article.domain;

public final class ArticleQuestion {

    private static final int DEFAULT_MIN_WORDS = 40;

    private final ArticleQuestionId id;
    private final ArticleReadingId articleReadingId;
    private final String questionText;
    private final int orderIndex;
    private final int minWords;
    private final String hintText;

    private ArticleQuestion(ArticleQuestionId id, ArticleReadingId articleReadingId,
                             String questionText, int orderIndex, int minWords, String hintText) {
        this.id = id;
        this.articleReadingId = articleReadingId;
        this.questionText = questionText;
        this.orderIndex = orderIndex;
        this.minWords = minWords;
        this.hintText = hintText;
    }

    public static ArticleQuestion create(ArticleReadingId articleReadingId, String questionText,
                                          int orderIndex, String hintText) {
        return new ArticleQuestion(ArticleQuestionId.generate(), articleReadingId,
                questionText, orderIndex, DEFAULT_MIN_WORDS, hintText);
    }

    public static ArticleQuestion reconstitute(ArticleQuestionId id, ArticleReadingId articleReadingId,
                                                String questionText, int orderIndex, int minWords, String hintText) {
        return new ArticleQuestion(id, articleReadingId, questionText, orderIndex, minWords, hintText);
    }

    public ArticleQuestionId id() { return id; }
    public ArticleReadingId articleReadingId() { return articleReadingId; }
    public String questionText() { return questionText; }
    public int orderIndex() { return orderIndex; }
    public int minWords() { return minWords; }
    public String hintText() { return hintText; }
}

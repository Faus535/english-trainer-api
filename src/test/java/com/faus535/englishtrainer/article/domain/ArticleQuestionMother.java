package com.faus535.englishtrainer.article.domain;

public final class ArticleQuestionMother {

    public static ArticleQuestion withHint(ArticleReadingId articleReadingId) {
        return ArticleQuestion.create(articleReadingId,
                "What is the main argument of the article?", 0,
                "Think about the central theme discussed in the opening paragraph.");
    }

    public static ArticleQuestion ordered(ArticleReadingId articleReadingId, int orderIndex) {
        return ArticleQuestion.create(articleReadingId,
                "Question " + orderIndex + ": describe the impact.", orderIndex,
                "Hint for question " + orderIndex);
    }
}

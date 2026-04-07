package com.faus535.englishtrainer.article.domain;

import java.util.UUID;

public final class ArticleMarkedWordMother {

    public static ArticleMarkedWord withWord(ArticleReadingId articleReadingId, UUID userId, String word) {
        return ArticleMarkedWord.create(articleReadingId, userId, word,
                "traducción de " + word, "Context sentence with " + word + ".");
    }

    public static ArticleMarkedWord defaults(ArticleReadingId articleReadingId, UUID userId) {
        return withWord(articleReadingId, userId, "spark debate");
    }
}

package com.faus535.englishtrainer.article.domain;

import java.util.List;
import java.util.UUID;

public final class ArticleMarkedWordMother {

    public static ArticleMarkedWord withWord(ArticleReadingId articleReadingId, UUID userId, String word) {
        return ArticleMarkedWord.create(articleReadingId, userId, word,
                "traducción de " + word, "brief English definition", "Context sentence with " + word + ".");
    }

    public static ArticleMarkedWord defaults(ArticleReadingId articleReadingId, UUID userId) {
        return withWord(articleReadingId, userId, "spark debate");
    }

    public static ArticleMarkedWord enriched(ArticleReadingId articleReadingId, UUID userId, String word) {
        ArticleMarkedWord base = withWord(articleReadingId, userId, word);
        return base.enrich(
                "To cause or provoke a discussion or argument",
                "/spɑːrk dɪˈbeɪt/",
                List.of("ignite discussion", "stir controversy", "provoke argument"),
                "The new reforms are likely to spark debate in parliament.",
                "verb phrase"
        );
    }

    public static ArticleMarkedWord withEnrichment(String definition, String phonetics,
                                                    List<String> synonyms, String exampleSentence,
                                                    String partOfSpeech) {
        ArticleReadingId articleReadingId = new ArticleReadingId(UUID.randomUUID());
        UUID userId = UUID.randomUUID();
        ArticleMarkedWord base = withWord(articleReadingId, userId, "spark debate");
        return base.enrich(definition, phonetics, synonyms, exampleSentence, partOfSpeech);
    }
}

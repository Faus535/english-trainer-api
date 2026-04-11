package com.faus535.englishtrainer.article.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ArticleMarkedWord {

    private final ArticleMarkedWordId id;
    private final ArticleReadingId articleReadingId;
    private final UUID userId;
    private final String wordOrPhrase;
    private final String translation;
    private final String englishDefinition;
    private final String contextSentence;
    private final Instant createdAt;
    private final String definition;
    private final String phonetics;
    private final List<String> synonyms;
    private final String exampleSentence;
    private final String partOfSpeech;

    private ArticleMarkedWord(ArticleMarkedWordId id, ArticleReadingId articleReadingId,
                               UUID userId, String wordOrPhrase, String translation,
                               String englishDefinition, String contextSentence, Instant createdAt,
                               String definition, String phonetics, List<String> synonyms,
                               String exampleSentence, String partOfSpeech) {
        this.id = id;
        this.articleReadingId = articleReadingId;
        this.userId = userId;
        this.wordOrPhrase = wordOrPhrase;
        this.translation = translation;
        this.englishDefinition = englishDefinition;
        this.contextSentence = contextSentence;
        this.createdAt = createdAt;
        this.definition = definition;
        this.phonetics = phonetics;
        this.synonyms = synonyms;
        this.exampleSentence = exampleSentence;
        this.partOfSpeech = partOfSpeech;
    }

    public static ArticleMarkedWord create(ArticleReadingId articleReadingId, UUID userId,
                                            String wordOrPhrase, String translation,
                                            String englishDefinition, String contextSentence) {
        return new ArticleMarkedWord(ArticleMarkedWordId.generate(), articleReadingId, userId,
                wordOrPhrase, translation, englishDefinition, contextSentence, Instant.now(),
                null, null, null, null, null);
    }

    public static ArticleMarkedWord reconstitute(ArticleMarkedWordId id, ArticleReadingId articleReadingId,
                                                  UUID userId, String wordOrPhrase, String translation,
                                                  String englishDefinition, String contextSentence, Instant createdAt,
                                                  String definition, String phonetics, List<String> synonyms,
                                                  String exampleSentence, String partOfSpeech) {
        return new ArticleMarkedWord(id, articleReadingId, userId, wordOrPhrase, translation,
                englishDefinition, contextSentence, createdAt,
                definition, phonetics, synonyms, exampleSentence, partOfSpeech);
    }

    public ArticleMarkedWord enrich(String definition, String phonetics, List<String> synonyms,
                                    String exampleSentence, String partOfSpeech) {
        return new ArticleMarkedWord(id, articleReadingId, userId, wordOrPhrase,
                translation, englishDefinition, contextSentence, createdAt,
                definition, phonetics, synonyms != null ? List.copyOf(synonyms) : null,
                exampleSentence, partOfSpeech);
    }

    public ArticleMarkedWordId id() { return id; }
    public ArticleReadingId articleReadingId() { return articleReadingId; }
    public UUID userId() { return userId; }
    public String wordOrPhrase() { return wordOrPhrase; }
    public String translation() { return translation; }
    public String englishDefinition() { return englishDefinition; }
    public String contextSentence() { return contextSentence; }
    public Instant createdAt() { return createdAt; }
    public String definition() { return definition; }
    public String phonetics() { return phonetics; }
    public List<String> synonyms() { return synonyms; }
    public String exampleSentence() { return exampleSentence; }
    public String partOfSpeech() { return partOfSpeech; }
}

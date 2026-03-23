package com.faus535.englishtrainer.vocabularycontext.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;

import java.time.Instant;

public final class VocabularyContext extends AggregateRoot<VocabularyContextId> {

    private final VocabularyContextId id;
    private final VocabEntryId vocabularyId;
    private final String level;
    private final String sentencesJson;
    private final Instant generatedAt;

    private VocabularyContext(VocabularyContextId id, VocabEntryId vocabularyId, String level,
                              String sentencesJson, Instant generatedAt) {
        this.id = id;
        this.vocabularyId = vocabularyId;
        this.level = level;
        this.sentencesJson = sentencesJson;
        this.generatedAt = generatedAt;
    }

    public static VocabularyContext create(VocabEntryId vocabularyId, String level, String sentencesJson) {
        return new VocabularyContext(
                VocabularyContextId.generate(),
                vocabularyId,
                level,
                sentencesJson,
                Instant.now()
        );
    }

    public static VocabularyContext reconstitute(VocabularyContextId id, VocabEntryId vocabularyId,
                                                  String level, String sentencesJson, Instant generatedAt) {
        return new VocabularyContext(id, vocabularyId, level, sentencesJson, generatedAt);
    }

    public VocabularyContextId id() { return id; }
    public VocabEntryId vocabularyId() { return vocabularyId; }
    public String level() { return level; }
    public String sentencesJson() { return sentencesJson; }
    public Instant generatedAt() { return generatedAt; }
}

package com.faus535.englishtrainer.vocabularycontext.infrastructure.persistence;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabularycontext.domain.VocabularyContext;
import com.faus535.englishtrainer.vocabularycontext.domain.VocabularyContextId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vocabulary_context")
class VocabularyContextEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "vocabulary_id", nullable = false)
    private UUID vocabularyId;

    @Column(nullable = false, length = 5)
    private String level;

    @Column(name = "sentences_json", nullable = false, columnDefinition = "TEXT")
    private String sentencesJson;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    protected VocabularyContextEntity() {}

    static VocabularyContextEntity fromAggregate(VocabularyContext aggregate) {
        VocabularyContextEntity entity = new VocabularyContextEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.vocabularyId = aggregate.vocabularyId().value();
        entity.level = aggregate.level();
        entity.sentencesJson = aggregate.sentencesJson();
        entity.generatedAt = aggregate.generatedAt();
        return entity;
    }

    VocabularyContext toAggregate() {
        return VocabularyContext.reconstitute(
                new VocabularyContextId(id),
                new VocabEntryId(vocabularyId),
                level,
                sentencesJson,
                generatedAt
        );
    }

    void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

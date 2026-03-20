package com.faus535.englishtrainer.phrase.infrastructure.persistence;

import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.phrase.domain.PhraseId;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "phrases")
class PhraseEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(nullable = false)
    private String en;

    @Column(nullable = false)
    private String es;

    @Column(nullable = false)
    private String level;

    protected PhraseEntity() {}

    static PhraseEntity fromAggregate(Phrase aggregate) {
        PhraseEntity entity = new PhraseEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.en = aggregate.en();
        entity.es = aggregate.es();
        entity.level = aggregate.level().value();
        return entity;
    }

    Phrase toAggregate() {
        return Phrase.reconstitute(
                new PhraseId(id),
                en,
                es,
                new VocabLevel(level)
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

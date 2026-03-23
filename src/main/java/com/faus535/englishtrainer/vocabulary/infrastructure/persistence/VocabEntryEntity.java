package com.faus535.englishtrainer.vocabulary.infrastructure.persistence;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
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
@Table(name = "vocab_entries")
class VocabEntryEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(nullable = false)
    private String en;

    private String ipa;

    @Column(nullable = false)
    private String es;

    private String type;

    private String example;

    @Column(nullable = false)
    private String level;

    private String category;

    private Integer block;

    @Column(name = "block_title")
    private String blockTitle;

    protected VocabEntryEntity() {}

    static VocabEntryEntity fromAggregate(VocabEntry aggregate) {
        VocabEntryEntity entity = new VocabEntryEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.en = aggregate.en();
        entity.ipa = aggregate.ipa();
        entity.es = aggregate.es();
        entity.type = aggregate.type();
        entity.example = aggregate.example();
        entity.level = aggregate.level().value();
        entity.category = aggregate.category();
        entity.block = aggregate.block();
        entity.blockTitle = aggregate.blockTitle();
        return entity;
    }

    VocabEntry toAggregate() {
        return VocabEntry.create(
                new VocabEntryId(id),
                en,
                ipa,
                es,
                type,
                example,
                new VocabLevel(level),
                category,
                block,
                blockTitle
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

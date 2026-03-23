package com.faus535.englishtrainer.minimalpair.infrastructure.persistence;

import com.faus535.englishtrainer.minimalpair.domain.MinimalPair;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "minimal_pairs")
class MinimalPairEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String word1;

    @Column(nullable = false)
    private String word2;

    @Column(nullable = false)
    private String ipa1;

    @Column(nullable = false)
    private String ipa2;

    @Column(name = "sound_category", nullable = false)
    private String soundCategory;

    @Column(nullable = false)
    private String level;

    protected MinimalPairEntity() {}

    MinimalPair toAggregate() {
        return MinimalPair.reconstitute(
                new MinimalPairId(id),
                word1, word2, ipa1, ipa2, soundCategory, level
        );
    }

    UUID getId() { return id; }
}

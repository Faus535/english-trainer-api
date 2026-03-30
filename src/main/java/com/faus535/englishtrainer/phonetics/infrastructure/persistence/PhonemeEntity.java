package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import com.faus535.englishtrainer.phonetics.domain.PhonemeCategory;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "phonemes")
class PhonemeEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String subcategory;

    @Column(name = "example_words", nullable = false)
    private String exampleWords;

    @Column
    private String description;

    @Column(name = "mouth_position")
    private String mouthPosition;

    @Column
    private String tips;

    @Column(name = "difficulty_order", nullable = false)
    private int difficultyOrder;

    protected PhonemeEntity() {}

    Phoneme toAggregate() {
        return Phoneme.reconstitute(
            new PhonemeId(id), symbol, name,
            PhonemeCategory.fromString(category), subcategory,
            parseJsonArray(exampleWords), description, mouthPosition, tips, difficultyOrder
        );
    }

    private static List<String> parseJsonArray(String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    UUID getId() { return id; }
}

package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import com.faus535.englishtrainer.phonetics.domain.PhonemeCategory;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "phonemes")
class PhonemeEntity {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    private String subcategory;

    @Column(name = "difficulty_order", nullable = false)
    private int difficultyOrder;

    @Column(name = "example_words", nullable = false)
    private String exampleWords;

    private String description;

    @Column(name = "mouth_position")
    private String mouthPosition;

    private String tips;

    protected PhonemeEntity() {}

    Phoneme toAggregate() {
        return Phoneme.reconstitute(
                new PhonemeId(id), symbol, name,
                PhonemeCategory.fromString(category), subcategory,
                difficultyOrder, parseJsonArray(exampleWords),
                description, mouthPosition, tips
        );
    }

    private List<String> parseJsonArray(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    UUID getId() { return id; }
}

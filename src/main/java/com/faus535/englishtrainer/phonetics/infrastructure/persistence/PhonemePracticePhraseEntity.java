package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhrase;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhraseId;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "phoneme_practice_phrases")
class PhonemePracticePhraseEntity {
    @Id
    private UUID id;

    @Column(name = "phoneme_id", nullable = false)
    private UUID phonemeId;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String difficulty;

    @Column(name = "target_words", nullable = false)
    private String targetWords;

    protected PhonemePracticePhraseEntity() {}

    PhonemePracticePhrase toAggregate() {
        return PhonemePracticePhrase.reconstitute(
            new PhonemePracticePhraseId(id), new PhonemeId(phonemeId),
            text, difficulty, parseJsonArray(targetWords)
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

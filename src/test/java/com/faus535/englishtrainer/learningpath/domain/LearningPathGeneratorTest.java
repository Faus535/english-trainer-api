package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.curriculum.domain.UnitDefinition;
import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class LearningPathGeneratorTest {

    private final UserProfileId userId = UserProfileId.generate();
    private final VocabLevel a1Level = new VocabLevel("a1");

    @Test
    void shouldCreateUnitsGroupedByBlock() {
        List<VocabEntry> vocabEntries = new ArrayList<>();
        for (int block = 1; block <= 3; block++) {
            for (int i = 0; i < 5; i++) {
                vocabEntries.add(VocabEntry.create(
                        VocabEntryId.generate(),
                        "word_" + block + "_" + i, "/wɜːd/", "palabra", "noun",
                        "Example sentence", a1Level, "greetings", block, "Block " + block
                ));
            }
        }

        LearningPathGenerator.GenerationResult result = LearningPathGenerator.generate(
                userId, "a1", vocabEntries, List.of(), List.of(), Set.of());

        assertNotNull(result.path());
        assertEquals(3, result.units().size());
        assertEquals(userId, result.path().userId());
        assertEquals("a1", result.path().currentLevel());

        for (int i = 0; i < result.units().size(); i++) {
            LearningUnit unit = result.units().get(i);
            assertEquals(i, unit.unitIndex());
            assertEquals("a1", unit.targetLevel());
            assertEquals(5, unit.contents().stream()
                    .filter(c -> c.contentType() == ContentType.VOCAB).count());
        }
    }

    @Test
    void shouldGroupByCategoryWhenNoBlocks() {
        List<VocabEntry> vocabEntries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            vocabEntries.add(VocabEntry.create(
                    VocabEntryId.generate(),
                    "food_" + i, "/fuːd/", "comida", "noun",
                    "Example", a1Level, "food", null, null
            ));
        }
        for (int i = 0; i < 3; i++) {
            vocabEntries.add(VocabEntry.create(
                    VocabEntryId.generate(),
                    "travel_" + i, "/ˈtræv.əl/", "viaje", "noun",
                    "Example", a1Level, "travel", null, null
            ));
        }

        LearningPathGenerator.GenerationResult result = LearningPathGenerator.generate(
                userId, "a1", vocabEntries, List.of(), List.of(), Set.of());

        assertEquals(2, result.units().size());
        assertEquals(5, result.units().get(0).contents().size());
        assertEquals(3, result.units().get(1).contents().size());
    }

    @Test
    void shouldDistributePhrasesAcrossUnits() {
        List<VocabEntry> vocabEntries = new ArrayList<>();
        for (int block = 1; block <= 2; block++) {
            for (int i = 0; i < 3; i++) {
                vocabEntries.add(VocabEntry.create(
                        VocabEntryId.generate(),
                        "word_" + block + "_" + i, "/wɜːd/", "palabra", "noun",
                        "Example", a1Level, "greetings", block, "Block " + block
                ));
            }
        }

        List<Phrase> phrases = List.of(
                Phrase.create("Hello", "Hola", a1Level),
                Phrase.create("Goodbye", "Adios", a1Level),
                Phrase.create("Thank you", "Gracias", a1Level),
                Phrase.create("Please", "Por favor", a1Level)
        );

        LearningPathGenerator.GenerationResult result = LearningPathGenerator.generate(
                userId, "a1", vocabEntries, phrases, List.of(), Set.of());

        assertEquals(2, result.units().size());

        long totalPhraseContents = result.units().stream()
                .flatMap(u -> u.contents().stream())
                .filter(c -> c.contentType() == ContentType.PHRASE)
                .count();
        assertEquals(4, totalPhraseContents);
    }

    @Test
    void shouldMarkAlreadyPracticedContent() {
        UUID practicedVocabId = UUID.randomUUID();
        VocabEntry practicedEntry = VocabEntry.create(
                new VocabEntryId(practicedVocabId),
                "hello", "/həˈloʊ/", "hola", "interjection",
                "Hello world", a1Level, "greetings", 1, "Block 1"
        );
        VocabEntry unpracticedEntry = VocabEntry.create(
                VocabEntryId.generate(),
                "goodbye", "/ɡʊdˈbaɪ/", "adios", "interjection",
                "Goodbye world", a1Level, "greetings", 1, "Block 1"
        );

        LearningPathGenerator.GenerationResult result = LearningPathGenerator.generate(
                userId, "a1", List.of(practicedEntry, unpracticedEntry),
                List.of(), List.of(), Set.of(practicedVocabId));

        assertEquals(1, result.units().size());
        LearningUnit unit = result.units().get(0);

        UnitContent practiced = unit.contents().stream()
                .filter(c -> c.contentId().equals(practicedVocabId))
                .findFirst()
                .orElseThrow();
        assertTrue(practiced.practiced());

        UnitContent unpracticed = unit.contents().stream()
                .filter(c -> !c.contentId().equals(practicedVocabId))
                .findFirst()
                .orElseThrow();
        assertFalse(unpracticed.practiced());
    }

    @Test
    void shouldUseCurriculumUnitTitles() {
        List<VocabEntry> vocabEntries = new ArrayList<>();
        for (int block = 1; block <= 2; block++) {
            for (int i = 0; i < 3; i++) {
                vocabEntries.add(VocabEntry.create(
                        VocabEntryId.generate(),
                        "word_" + block + "_" + i, "/wɜːd/", "palabra", "noun",
                        "Example", a1Level, "greetings", block, "Block " + block
                ));
            }
        }

        List<UnitDefinition> curriculumUnits = List.of(
                new UnitDefinition(0, "Greetings & Introductions", "vocabulary"),
                new UnitDefinition(1, "Family & Relationships", "vocabulary")
        );

        LearningPathGenerator.GenerationResult result = LearningPathGenerator.generate(
                userId, "a1", vocabEntries, List.of(), curriculumUnits, Set.of());

        assertEquals("Greetings & Introductions", result.units().get(0).unitName());
        assertEquals("Family & Relationships", result.units().get(1).unitName());
    }

    @Test
    void shouldPartitionLargeBlocksIntoMultipleUnits() {
        List<VocabEntry> vocabEntries = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            vocabEntries.add(VocabEntry.create(
                    VocabEntryId.generate(),
                    "word_" + i, "/wɜːd/", "palabra", "noun",
                    "Example", a1Level, "greetings", 1, "Block 1"
            ));
        }

        LearningPathGenerator.GenerationResult result = LearningPathGenerator.generate(
                userId, "a1", vocabEntries, List.of(), List.of(), Set.of());

        assertEquals(3, result.units().size());
        assertEquals(20, result.units().get(0).contents().size());
        assertEquals(20, result.units().get(1).contents().size());
        assertEquals(5, result.units().get(2).contents().size());
    }

    @Test
    void shouldCreatePathWithCorrectUnitIds() {
        List<VocabEntry> vocabEntries = new ArrayList<>();
        for (int block = 1; block <= 2; block++) {
            vocabEntries.add(VocabEntry.create(
                    VocabEntryId.generate(),
                    "word_" + block, "/wɜːd/", "palabra", "noun",
                    "Example", a1Level, "greetings", block, "Block " + block
            ));
        }

        LearningPathGenerator.GenerationResult result = LearningPathGenerator.generate(
                userId, "a1", vocabEntries, List.of(), List.of(), Set.of());

        List<LearningUnitId> pathUnitIds = result.path().unitIds();
        List<LearningUnitId> unitIds = result.units().stream().map(LearningUnit::id).toList();
        assertEquals(unitIds, pathUnitIds);
    }
}

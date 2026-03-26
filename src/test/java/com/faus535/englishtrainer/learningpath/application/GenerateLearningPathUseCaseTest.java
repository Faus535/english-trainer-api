package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.curriculum.domain.CurriculumBlock;
import com.faus535.englishtrainer.curriculum.domain.CurriculumProvider;
import com.faus535.englishtrainer.curriculum.domain.IntegratorDefinition;
import com.faus535.englishtrainer.curriculum.domain.ModuleDefinition;
import com.faus535.englishtrainer.curriculum.domain.UnitDefinition;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningPathRepository;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningUnitRepository;
import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.phrase.domain.PhraseRepository;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

final class GenerateLearningPathUseCaseTest {

    private InMemoryLearningPathRepository learningPathRepository;
    private InMemoryLearningUnitRepository learningUnitRepository;
    private InMemoryUserProfileRepository userProfileRepository;
    private StubVocabRepository vocabRepository;
    private StubPhraseRepository phraseRepository;
    private StubCurriculumProvider curriculumProvider;
    private StubSpacedRepetitionRepository spacedRepetitionRepository;
    private GenerateLearningPathUseCase useCase;

    @BeforeEach
    void setUp() {
        learningPathRepository = new InMemoryLearningPathRepository();
        learningUnitRepository = new InMemoryLearningUnitRepository();
        userProfileRepository = new InMemoryUserProfileRepository();
        vocabRepository = new StubVocabRepository();
        phraseRepository = new StubPhraseRepository();
        curriculumProvider = new StubCurriculumProvider();
        spacedRepetitionRepository = new StubSpacedRepetitionRepository();

        useCase = new GenerateLearningPathUseCase(
                learningPathRepository,
                learningUnitRepository,
                userProfileRepository,
                vocabRepository,
                phraseRepository,
                curriculumProvider,
                spacedRepetitionRepository
        );
    }

    @Test
    void shouldGenerateLearningPathForUser() throws Exception {
        UserProfile profile = UserProfile.create();
        userProfileRepository.save(profile);

        VocabLevel level = new VocabLevel(profile.levelVocabulary().value());
        for (int block = 1; block <= 3; block++) {
            for (int i = 0; i < 5; i++) {
                vocabRepository.entries.add(VocabEntry.create(
                        VocabEntryId.generate(),
                        "word_" + block + "_" + i, "/wɜːd/", "palabra", "noun",
                        "Example", level, "greetings", block, "Block " + block
                ));
            }
        }

        phraseRepository.phrases.add(Phrase.create("Hello", "Hola", level));
        phraseRepository.phrases.add(Phrase.create("Goodbye", "Adios", level));

        GenerateLearningPathUseCase.GenerationSummary summary = useCase.execute(profile.id());

        assertNotNull(summary.pathId());
        assertEquals(profile.levelVocabulary().value(), summary.level());
        assertEquals(3, summary.unitCount());
        assertTrue(summary.totalContentItems() > 0);
        assertTrue(learningPathRepository.findByUserId(profile.id()).isPresent());
    }

    @Test
    void shouldDeleteExistingPathBeforeGenerating() throws Exception {
        UserProfile profile = UserProfile.create();
        userProfileRepository.save(profile);

        VocabLevel level = new VocabLevel(profile.levelVocabulary().value());
        vocabRepository.entries.add(VocabEntry.create(
                VocabEntryId.generate(),
                "hello", "/həˈloʊ/", "hola", "interjection",
                "Hello", level, "greetings", 1, "Block 1"
        ));

        useCase.execute(profile.id());
        assertTrue(learningPathRepository.findByUserId(profile.id()).isPresent());

        vocabRepository.entries.add(VocabEntry.create(
                VocabEntryId.generate(),
                "world", "/wɜːrld/", "mundo", "noun",
                "World", level, "greetings", 1, "Block 1"
        ));

        GenerateLearningPathUseCase.GenerationSummary summary = useCase.execute(profile.id());

        assertNotNull(summary.pathId());
        assertTrue(learningPathRepository.findByUserId(profile.id()).isPresent());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        UserProfileId unknownUser = UserProfileId.generate();

        assertThrows(UserProfileNotFoundException.class, () -> useCase.execute(unknownUser));
    }

    // Stub implementations for testing

    private static class StubVocabRepository implements VocabRepository {
        final List<VocabEntry> entries = new ArrayList<>();

        @Override
        public List<VocabEntry> findAll() { return entries; }

        @Override
        public List<VocabEntry> findByLevel(VocabLevel level) {
            return entries.stream()
                    .filter(e -> e.level().equals(level))
                    .toList();
        }

        @Override
        public List<VocabEntry> searchByWord(String query) { return List.of(); }

        @Override
        public VocabEntry save(VocabEntry entry) { entries.add(entry); return entry; }

        @Override
        public List<VocabEntry> findRandom(int count, VocabLevel level) { return List.of(); }

        @Override
        public List<VocabEntry> findByLevelAndBlock(VocabLevel level, int block) {
            return entries.stream()
                    .filter(e -> e.level().equals(level) && e.block() != null && e.block() == block)
                    .toList();
        }

        @Override
        public List<VocabEntry> findByIds(java.util.List<java.util.UUID> ids) { return List.of(); }

        @Override
        public List<VocabEntry> findByLevelExcludingIds(VocabLevel level, java.util.Set<java.util.UUID> excludeIds, int count) {
            return entries.stream()
                    .filter(e -> e.level().equals(level))
                    .filter(e -> excludeIds == null || !excludeIds.contains(e.id().value()))
                    .limit(count)
                    .toList();
        }
    }

    private static class StubPhraseRepository implements PhraseRepository {
        final List<Phrase> phrases = new ArrayList<>();

        @Override
        public List<Phrase> findByLevel(VocabLevel level) {
            return phrases.stream()
                    .filter(p -> p.level().equals(level))
                    .toList();
        }

        @Override
        public List<Phrase> findRandom(int count, VocabLevel level) { return List.of(); }

        @Override
        public Phrase save(Phrase phrase) { phrases.add(phrase); return phrase; }
    }

    private static class StubCurriculumProvider implements CurriculumProvider {
        @Override
        public List<CurriculumBlock> getPlan() { return List.of(); }

        @Override
        public Optional<CurriculumBlock> getBlock(int blockNumber) { return Optional.empty(); }

        @Override
        public List<ModuleDefinition> getModules() { return List.of(); }

        @Override
        public Optional<ModuleDefinition> getModule(String name) { return Optional.empty(); }

        @Override
        public List<IntegratorDefinition> getIntegrators(String level) { return List.of(); }

        @Override
        public List<IntegratorDefinition> getAllIntegrators() { return List.of(); }
    }

    private static class StubSpacedRepetitionRepository implements SpacedRepetitionRepository {
        @Override
        public Optional<SpacedRepetitionItem> findById(SpacedRepetitionItemId id) { return Optional.empty(); }

        @Override
        public Optional<SpacedRepetitionItem> findByUserAndUnitReference(UserProfileId userId, String unitReference) { return Optional.empty(); }

        @Override
        public List<SpacedRepetitionItem> findDueByUser(UserProfileId userId, LocalDate today) { return List.of(); }

        @Override
        public List<SpacedRepetitionItem> findAllByUser(UserProfileId userId) { return List.of(); }

        @Override
        public SpacedRepetitionItem save(SpacedRepetitionItem item) { return item; }
    }
}

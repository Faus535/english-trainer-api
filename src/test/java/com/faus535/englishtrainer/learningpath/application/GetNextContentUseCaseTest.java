package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.learningpath.domain.*;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningPathRepository;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningUnitRepository;
import com.faus535.englishtrainer.spacedrepetition.infrastructure.InMemorySpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetNextContentUseCaseTest {

    private InMemoryLearningPathRepository learningPathRepository;
    private InMemoryLearningUnitRepository learningUnitRepository;
    private InMemorySpacedRepetitionRepository spacedRepetitionRepository;
    private InMemoryVocabRepository vocabRepository;
    private GetNextContentUseCase useCase;

    @BeforeEach
    void setUp() {
        learningPathRepository = new InMemoryLearningPathRepository();
        learningUnitRepository = new InMemoryLearningUnitRepository();
        spacedRepetitionRepository = new InMemorySpacedRepetitionRepository();
        vocabRepository = new InMemoryVocabRepository();
        useCase = new GetNextContentUseCase(
                learningPathRepository,
                learningUnitRepository,
                spacedRepetitionRepository,
                vocabRepository
        );
    }

    @Test
    void should_return_content_ids_from_learning_path() {
        UserProfileId userId = UserProfileId.generate();
        LearningPathId pathId = LearningPathId.generate();

        UUID contentId1 = UUID.randomUUID();
        UUID contentId2 = UUID.randomUUID();
        UUID contentId3 = UUID.randomUUID();

        List<UnitContent> contents = List.of(
                new UnitContent(ContentType.VOCAB, contentId1, false, null),
                new UnitContent(ContentType.VOCAB, contentId2, false, null),
                new UnitContent(ContentType.VOCAB, contentId3, false, null)
        );

        LearningUnit unit = LearningUnit.create(pathId, 0, "Unit 1", "A1", contents);
        learningUnitRepository.save(unit);

        LearningPath path = LearningPath.create(userId, "A1", List.of(unit.id()));
        learningPathRepository.save(path);

        List<UUID> result = useCase.execute(userId, ContentType.VOCAB, 3);

        assertFalse(result.isEmpty());
        assertTrue(result.size() <= 3);
    }

    @Test
    void should_fallback_to_random_when_no_learning_path() {
        UserProfileId userId = UserProfileId.generate();

        VocabEntry entry1 = createVocabEntry("hello", "A1");
        VocabEntry entry2 = createVocabEntry("world", "A1");
        vocabRepository.save(entry1);
        vocabRepository.save(entry2);

        List<UUID> result = useCase.execute(userId, ContentType.VOCAB, 5);

        assertNotNull(result);
    }

    @Test
    void should_fallback_when_path_is_completed() {
        UserProfileId userId = UserProfileId.generate();

        LearningPath path = LearningPath.reconstitute(
                LearningPathId.generate(),
                userId,
                "A1",
                1,
                List.of(LearningUnitId.generate()),
                Instant.now(),
                Instant.now()
        );
        learningPathRepository.save(path);

        VocabEntry entry = createVocabEntry("fallback", "A1");
        vocabRepository.save(entry);

        List<UUID> result = useCase.execute(userId, ContentType.VOCAB, 3);

        assertNotNull(result);
    }

    @Test
    void should_return_unpracticed_content_only() {
        UserProfileId userId = UserProfileId.generate();
        LearningPathId pathId = LearningPathId.generate();

        UUID practicedId = UUID.randomUUID();
        UUID unpracticedId = UUID.randomUUID();

        List<UnitContent> contents = List.of(
                new UnitContent(ContentType.VOCAB, practicedId, true, Instant.now().minusSeconds(86400 * 2)),
                new UnitContent(ContentType.VOCAB, unpracticedId, false, null)
        );

        LearningUnit unit = LearningUnit.create(pathId, 0, "Unit 1", "A1", contents);
        learningUnitRepository.save(unit);

        LearningPath path = LearningPath.create(userId, "A1", List.of(unit.id()));
        learningPathRepository.save(path);

        List<UUID> result = useCase.execute(userId, ContentType.VOCAB, 5);

        assertFalse(result.isEmpty());
        assertTrue(result.contains(unpracticedId));
    }

    private VocabEntry createVocabEntry(String word, String level) {
        return VocabEntry.create(
                VocabEntryId.generate(),
                word,
                null,
                "translation",
                "noun",
                "example",
                new VocabLevel(level)
        );
    }
}

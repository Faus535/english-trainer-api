package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.learningpath.domain.ContentType;
import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;
import com.faus535.englishtrainer.learningpath.domain.MasteryScore;
import com.faus535.englishtrainer.learningpath.domain.UnitContent;
import com.faus535.englishtrainer.learningpath.domain.UnitStatus;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathNotFoundException;
import com.faus535.englishtrainer.learningpath.domain.error.LearningUnitNotFoundException;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningPathRepository;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningUnitRepository;
import com.faus535.englishtrainer.session.domain.ExerciseResult;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionBlock;
import com.faus535.englishtrainer.session.domain.SessionExercise;
import com.faus535.englishtrainer.session.domain.SessionMode;
import com.faus535.englishtrainer.session.domain.SessionType;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.session.infrastructure.InMemorySessionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordExerciseResultUseCaseTest {

    private InMemorySessionRepository sessionRepository;
    private InMemoryLearningPathRepository learningPathRepository;
    private InMemoryLearningUnitRepository learningUnitRepository;
    private RecordExerciseResultUseCase useCase;

    @BeforeEach
    void setUp() {
        sessionRepository = new InMemorySessionRepository();
        learningPathRepository = new InMemoryLearningPathRepository();
        learningUnitRepository = new InMemoryLearningUnitRepository();
        useCase = new RecordExerciseResultUseCase(
                sessionRepository, learningPathRepository, learningUnitRepository);
    }

    @Test
    void should_record_result_and_update_mastery() throws Exception {
        UserProfileId userId = UserProfileId.generate();
        UUID contentId = UUID.randomUUID();

        // Create session with exercises
        Session session = Session.create(
                userId,
                new SessionMode("full"),
                new SessionType("normal"),
                "listening", "vocabulary", null,
                List.of(new SessionBlock("warmup", "review", 3)),
                List.of(new SessionExercise(0, "VOCAB_QUIZ", List.of(contentId), 10, null))
        );
        sessionRepository.save(session);

        // Create learning path and unit
        LearningUnitId unitId = LearningUnitId.generate();
        LearningPath path = LearningPath.create(userId, "A1", List.of(unitId));
        learningPathRepository.save(path);

        LearningUnit unit = LearningUnit.reconstitute(
                unitId, path.id(), 0, "Greetings", "A1",
                UnitStatus.IN_PROGRESS, new MasteryScore(0),
                List.of(new UnitContent(ContentType.VOCAB, contentId, false, null)),
                null, Instant.now(), Instant.now()
        );
        learningUnitRepository.save(unit);

        // Execute
        ExerciseResult result = new ExerciseResult(8, 10, 500, Instant.now());
        RecordExerciseResultUseCase.RecordResult recordResult =
                useCase.execute(userId, session.id().value(), 0, result);

        // Verify
        assertTrue(recordResult.unitMasteryScore() > 0);
        assertEquals(16, recordResult.xpEarned()); // 8 * 2
    }

    @Test
    void should_update_mastery_to_mastered_when_score_is_high()
            throws Exception {
        UserProfileId userId = UserProfileId.generate();
        UUID contentId = UUID.randomUUID();

        Session session = Session.create(
                userId,
                new SessionMode("full"),
                new SessionType("normal"),
                "listening", "vocabulary", null,
                List.of(new SessionBlock("warmup", "review", 3)),
                List.of(new SessionExercise(0, "VOCAB_QUIZ", List.of(contentId), 10, null))
        );
        sessionRepository.save(session);

        LearningUnitId unitId = LearningUnitId.generate();
        LearningPath path = LearningPath.create(userId, "A1", List.of(unitId));
        learningPathRepository.save(path);

        LearningUnit unit = LearningUnit.reconstitute(
                unitId, path.id(), 0, "Greetings", "A1",
                UnitStatus.IN_PROGRESS, new MasteryScore(0),
                List.of(new UnitContent(ContentType.VOCAB, contentId, false, null)),
                null, Instant.now(), Instant.now()
        );
        learningUnitRepository.save(unit);

        // Perfect score
        ExerciseResult result = new ExerciseResult(10, 10, 300, Instant.now());
        RecordExerciseResultUseCase.RecordResult recordResult =
                useCase.execute(userId, session.id().value(), 0, result);

        assertEquals(100, recordResult.unitMasteryScore());
        assertEquals("MASTERED", recordResult.unitStatus());
    }

    @Test
    void should_throw_when_session_not_found() {
        UserProfileId userId = UserProfileId.generate();
        ExerciseResult result = new ExerciseResult(5, 10, 500, Instant.now());

        assertThrows(SessionNotFoundException.class, () ->
                useCase.execute(userId, UUID.randomUUID(), 0, result));
    }

    @Test
    void should_throw_when_session_belongs_to_different_user() {
        UserProfileId userId = UserProfileId.generate();
        UserProfileId otherUserId = UserProfileId.generate();

        Session session = Session.create(
                otherUserId,
                new SessionMode("full"),
                new SessionType("normal"),
                "listening", "vocabulary", null,
                List.of(new SessionBlock("warmup", "review", 3)),
                List.of(new SessionExercise(0, "VOCAB_QUIZ", List.of(), 10, null))
        );
        sessionRepository.save(session);

        ExerciseResult result = new ExerciseResult(5, 10, 500, Instant.now());

        assertThrows(SessionNotFoundException.class, () ->
                useCase.execute(userId, session.id().value(), 0, result));
    }
}

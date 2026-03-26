package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningPathRepository;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitRepository;
import com.faus535.englishtrainer.learningpath.domain.MasteryCalculator;
import com.faus535.englishtrainer.learningpath.domain.MasteryScore;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathNotFoundException;
import com.faus535.englishtrainer.learningpath.domain.error.LearningUnitNotFoundException;
import com.faus535.englishtrainer.session.domain.BlockProgress;
import com.faus535.englishtrainer.session.domain.ExerciseResult;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionExercise;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UseCase
public class RecordExerciseResultUseCase {

    private final SessionRepository sessionRepository;
    private final LearningPathRepository learningPathRepository;
    private final LearningUnitRepository learningUnitRepository;

    public RecordExerciseResultUseCase(SessionRepository sessionRepository,
                                       LearningPathRepository learningPathRepository,
                                       LearningUnitRepository learningUnitRepository) {
        this.sessionRepository = sessionRepository;
        this.learningPathRepository = learningPathRepository;
        this.learningUnitRepository = learningUnitRepository;
    }

    public record RecordResult(
            int unitMasteryScore,
            String unitStatus,
            int xpEarned,
            int blockIndex,
            boolean blockCompleted,
            int completedExercisesInBlock,
            int totalExercisesInBlock
    ) {}

    @Transactional
    public RecordResult execute(UserProfileId userId, UUID sessionId, int exerciseIndex,
                                ExerciseResult result)
            throws SessionNotFoundException, LearningPathNotFoundException, LearningUnitNotFoundException {

        // 1. Load Session and validate ownership
        Session session = sessionRepository.findById(new SessionId(sessionId))
                .orElseThrow(() -> new SessionNotFoundException(new SessionId(sessionId)));

        if (!session.userId().equals(userId)) {
            throw new SessionNotFoundException(new SessionId(sessionId));
        }

        // 2. Record ExerciseResult on Session
        Session updatedSession = session.recordExerciseResult(exerciseIndex, result);

        // 3. Save updated Session
        sessionRepository.save(updatedSession);

        // 4. Load LearningPath for user
        LearningPath learningPath = learningPathRepository.findByUserId(userId)
                .orElseThrow(() -> new LearningPathNotFoundException(
                        new com.faus535.englishtrainer.learningpath.domain.LearningPathId(UUID.randomUUID())));

        // 5. Load current LearningUnit
        if (learningPath.currentUnitId() == null) {
            SessionExercise ex = findExercise(updatedSession, exerciseIndex);
            int bi = ex != null ? ex.blockIndex() : 0;
            BlockProgress bp = updatedSession.getBlockProgress(bi);
            return new RecordResult(100, "MASTERED", result.correctCount() * 2,
                    bi, bp.isCompleted(), bp.completedExercises(), bp.totalExercises());
        }

        LearningUnit currentUnit = learningUnitRepository.findById(learningPath.currentUnitId())
                .orElseThrow(() -> new LearningUnitNotFoundException(learningPath.currentUnitId()));

        // 6. Mark practiced content IDs from the exercise
        SessionExercise exercise = findExercise(updatedSession, exerciseIndex);
        LearningUnit unitWithPracticed = currentUnit;
        if (exercise != null) {
            for (UUID contentId : exercise.contentIds()) {
                unitWithPracticed = unitWithPracticed.markContentPracticed(contentId);
            }
        }

        // 7. Calculate mastery using all exercise results for this unit
        List<ExerciseResult> allResults = new ArrayList<>();
        List<String> allTypes = new ArrayList<>();
        for (SessionExercise ex : updatedSession.exercises()) {
            if (ex.result() != null) {
                allResults.add(ex.result());
                allTypes.add(ex.exerciseType());
            }
        }

        // 8. Update unit mastery score
        MasteryScore masteryScore = MasteryCalculator.calculate(allResults, allTypes);
        LearningUnit updatedUnit = unitWithPracticed.updateMastery(masteryScore, userId);

        // 9. Save LearningUnit
        learningUnitRepository.save(updatedUnit);

        // 10. Calculate XP
        int xpEarned = result.correctCount() * 2;

        // 10.5. Compute block progress
        SessionExercise recordedExercise = findExercise(updatedSession, exerciseIndex);
        int exBlockIndex = recordedExercise != null ? recordedExercise.blockIndex() : 0;
        BlockProgress blockProgress = updatedSession.getBlockProgress(exBlockIndex);

        // 11. Return RecordResult
        return new RecordResult(
                masteryScore.value(),
                updatedUnit.status().name(),
                xpEarned,
                exBlockIndex,
                blockProgress.isCompleted(),
                blockProgress.completedExercises(),
                blockProgress.totalExercises()
        );
    }

    private SessionExercise findExercise(Session session, int exerciseIndex) {
        return session.exercises().stream()
                .filter(ex -> ex.exerciseIndex() == exerciseIndex)
                .findFirst()
                .orElse(null);
    }
}

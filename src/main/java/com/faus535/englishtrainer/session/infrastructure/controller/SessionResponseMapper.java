package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.domain.BlockProgress;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionExercise;

import java.util.List;
import java.util.stream.IntStream;

final class SessionResponseMapper {

    private SessionResponseMapper() {}

    static SessionResponse toResponse(Session session) {
        List<SessionBlockResponse> blockResponses = IntStream.range(0, session.blocks().size())
                .mapToObj(blockIdx -> {
                    var block = session.blocks().get(blockIdx);
                    BlockProgress progress = session.getBlockProgress(blockIdx);
                    List<SessionExercise> blockExercises = session.getExercisesForBlock(blockIdx);

                    List<SessionExerciseResponse> exerciseResponses = blockExercises.stream()
                            .map(SessionResponseMapper::mapExercise)
                            .toList();

                    return new SessionBlockResponse(
                            block.blockType(), block.moduleName(), block.durationMinutes(),
                            block.exerciseCount(), progress.completedExercises(),
                            progress.isCompleted(), exerciseResponses);
                })
                .toList();

        List<SessionExerciseResponse> allExercises = session.exercises().stream()
                .map(SessionResponseMapper::mapExercise)
                .toList();

        return new SessionResponse(
                session.id().value().toString(),
                session.userId().value().toString(),
                session.mode().value(),
                session.sessionType().value(),
                session.listeningModule(),
                session.secondaryModule(),
                session.integratorTheme(),
                blockResponses,
                allExercises,
                session.completed(),
                session.startedAt().toString(),
                session.completedAt() != null ? session.completedAt().toString() : null,
                session.durationMinutes()
        );
    }

    private static SessionExerciseResponse mapExercise(SessionExercise ex) {
        return new SessionExerciseResponse(
                ex.exerciseIndex(), ex.blockIndex(), ex.exerciseType(),
                ex.targetCount(), ex.isCompleted(),
                ex.result() != null ? ex.result().correctCount() : null,
                ex.result() != null ? ex.result().totalCount() : null);
    }
}

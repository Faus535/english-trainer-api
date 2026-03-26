package com.faus535.englishtrainer.session.domain;

import com.faus535.englishtrainer.session.domain.error.IncompleteSessionException;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.session.domain.event.SessionCompletedEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;
import java.util.List;

public final class Session extends AggregateRoot<SessionId> {

    private final SessionId id;
    private final UserProfileId userId;
    private final SessionMode mode;
    private final SessionType sessionType;
    private final String listeningModule;
    private final String secondaryModule;
    private final String integratorTheme;
    private final List<SessionBlock> blocks;
    private final List<SessionExercise> exercises;
    private final boolean completed;
    private final Instant startedAt;
    private final Instant completedAt;
    private final Integer durationMinutes;

    private Session(SessionId id, UserProfileId userId, SessionMode mode, SessionType sessionType,
                    String listeningModule, String secondaryModule, String integratorTheme,
                    List<SessionBlock> blocks, List<SessionExercise> exercises,
                    boolean completed, Instant startedAt,
                    Instant completedAt, Integer durationMinutes) {
        this.id = id;
        this.userId = userId;
        this.mode = mode;
        this.sessionType = sessionType;
        this.listeningModule = listeningModule;
        this.secondaryModule = secondaryModule;
        this.integratorTheme = integratorTheme;
        this.blocks = List.copyOf(blocks);
        this.exercises = exercises != null ? List.copyOf(exercises) : List.of();
        this.completed = completed;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationMinutes = durationMinutes;
    }

    public static Session create(UserProfileId userId, SessionMode mode, SessionType type,
                                 String listeningModule, String secondaryModule,
                                 String integratorTheme, List<SessionBlock> blocks) {
        return create(userId, mode, type, listeningModule, secondaryModule, integratorTheme, blocks, List.of());
    }

    public static Session create(UserProfileId userId, SessionMode mode, SessionType type,
                                 String listeningModule, String secondaryModule,
                                 String integratorTheme, List<SessionBlock> blocks,
                                 List<SessionExercise> exercises) {
        return new Session(
                SessionId.generate(),
                userId,
                mode,
                type,
                listeningModule,
                secondaryModule,
                integratorTheme,
                blocks,
                exercises,
                false,
                Instant.now(),
                null,
                null
        );
    }

    public static Session reconstitute(SessionId id, UserProfileId userId, SessionMode mode,
                                       SessionType sessionType, String listeningModule,
                                       String secondaryModule, String integratorTheme,
                                       List<SessionBlock> blocks, boolean completed,
                                       Instant startedAt, Instant completedAt,
                                       Integer durationMinutes) {
        return reconstitute(id, userId, mode, sessionType, listeningModule, secondaryModule,
                integratorTheme, blocks, List.of(), completed, startedAt, completedAt, durationMinutes);
    }

    public static Session reconstitute(SessionId id, UserProfileId userId, SessionMode mode,
                                       SessionType sessionType, String listeningModule,
                                       String secondaryModule, String integratorTheme,
                                       List<SessionBlock> blocks, List<SessionExercise> exercises,
                                       boolean completed, Instant startedAt, Instant completedAt,
                                       Integer durationMinutes) {
        return new Session(id, userId, mode, sessionType, listeningModule, secondaryModule,
                integratorTheme, blocks, exercises, completed, startedAt, completedAt, durationMinutes);
    }

    public Session complete(int durationMinutes) throws IncompleteSessionException {
        if (!areAllBlocksCompleted()) {
            throw new IncompleteSessionException();
        }
        Session completed = new Session(id, userId, mode, sessionType, listeningModule, secondaryModule,
                integratorTheme, blocks, exercises, true, startedAt, Instant.now(), durationMinutes);
        completed.registerEvent(new SessionCompletedEvent(id, userId));
        return completed;
    }

    public Session recordExerciseResult(int exerciseIndex, ExerciseResult result) {
        if (result.correctCount() > result.totalCount()) {
            throw new IllegalArgumentException("correctCount cannot exceed totalCount");
        }
        List<SessionExercise> updatedExercises = exercises.stream()
                .map(ex -> ex.exerciseIndex() == exerciseIndex ? ex.withResult(result) : ex)
                .toList();
        return new Session(id, userId, mode, sessionType, listeningModule, secondaryModule,
                integratorTheme, blocks, updatedExercises, completed, startedAt, completedAt, durationMinutes);
    }

    public boolean isBlockCompleted(int blockIndex) {
        List<SessionExercise> blockExercises = getExercisesForBlock(blockIndex);
        return blockExercises.stream().allMatch(SessionExercise::isCompleted);
    }

    public boolean areAllBlocksCompleted() {
        return exercises.stream().allMatch(SessionExercise::isCompleted);
    }

    public BlockProgress getBlockProgress(int blockIndex) {
        List<SessionExercise> blockExercises = getExercisesForBlock(blockIndex);
        int completed = (int) blockExercises.stream().filter(SessionExercise::isCompleted).count();
        return new BlockProgress(blockIndex, blockExercises.size(), completed);
    }

    public List<SessionExercise> getExercisesForBlock(int blockIndex) {
        return exercises.stream()
                .filter(ex -> ex.blockIndex() == blockIndex)
                .toList();
    }

    public SessionId id() { return id; }
    public UserProfileId userId() { return userId; }
    public SessionMode mode() { return mode; }
    public SessionType sessionType() { return sessionType; }
    public String listeningModule() { return listeningModule; }
    public String secondaryModule() { return secondaryModule; }
    public String integratorTheme() { return integratorTheme; }
    public List<SessionBlock> blocks() { return blocks; }
    public List<SessionExercise> exercises() { return exercises; }
    public boolean completed() { return completed; }
    public Instant startedAt() { return startedAt; }
    public Instant completedAt() { return completedAt; }
    public Integer durationMinutes() { return durationMinutes; }
}

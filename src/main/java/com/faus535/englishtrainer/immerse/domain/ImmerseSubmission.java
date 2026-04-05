package com.faus535.englishtrainer.immerse.domain;

import com.faus535.englishtrainer.immerse.domain.event.ImmerseExerciseAnsweredEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.UUID;

public final class ImmerseSubmission extends AggregateRoot<ImmerseSubmissionId> {

    private final ImmerseSubmissionId id;
    private final ImmerseExerciseId exerciseId;
    private final UUID userId;
    private final String userAnswer;
    private final boolean correct;
    private final String feedback;
    private final Instant submittedAt;

    private ImmerseSubmission(ImmerseSubmissionId id, ImmerseExerciseId exerciseId, UUID userId,
                              String userAnswer, boolean correct, String feedback, Instant submittedAt) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.userId = userId;
        this.userAnswer = userAnswer;
        this.correct = correct;
        this.feedback = feedback;
        this.submittedAt = submittedAt;
    }

    public static ImmerseSubmission create(UUID userId, ImmerseExercise exercise, String userAnswer) {
        boolean isCorrect = exercise.correctAnswer().equalsIgnoreCase(userAnswer.trim());
        String feedback = isCorrect
                ? "Correct!"
                : "The correct answer is: " + exercise.correctAnswer();

        ImmerseSubmission submission = new ImmerseSubmission(
                ImmerseSubmissionId.generate(), exercise.id(), userId,
                userAnswer, isCorrect, feedback, Instant.now());

        if (!isCorrect) {
            submission.registerEvent(new ImmerseExerciseAnsweredEvent(
                    userId, exercise.id(), exercise.question(), exercise.correctAnswer(), userAnswer));
        }

        return submission;
    }

    public static ImmerseSubmission reconstitute(ImmerseSubmissionId id, ImmerseExerciseId exerciseId,
                                                  UUID userId, String userAnswer, boolean correct,
                                                  String feedback, Instant submittedAt) {
        return new ImmerseSubmission(id, exerciseId, userId, userAnswer, correct, feedback, submittedAt);
    }

    public ImmerseSubmissionId id() { return id; }
    public ImmerseExerciseId exerciseId() { return exerciseId; }
    public UUID userId() { return userId; }
    public String userAnswer() { return userAnswer; }
    public boolean isCorrect() { return correct; }
    public String feedback() { return feedback; }
    public Instant submittedAt() { return submittedAt; }
}

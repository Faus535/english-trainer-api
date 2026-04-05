package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseExerciseNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class SubmitExerciseAnswerUseCase {

    private final ImmerseExerciseRepository exerciseRepository;
    private final ImmerseSubmissionRepository submissionRepository;
    private final ApplicationEventPublisher eventPublisher;

    SubmitExerciseAnswerUseCase(ImmerseExerciseRepository exerciseRepository,
                                 ImmerseSubmissionRepository submissionRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.exerciseRepository = exerciseRepository;
        this.submissionRepository = submissionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SubmitAnswerResult execute(UUID userId, UUID exerciseId, String userAnswer)
            throws ImmerseExerciseNotFoundException {

        ImmerseExerciseId exId = new ImmerseExerciseId(exerciseId);
        ImmerseExercise exercise = exerciseRepository.findById(exId)
                .orElseThrow(() -> new ImmerseExerciseNotFoundException(exId));

        ImmerseSubmission submission = ImmerseSubmission.create(userId, exercise, userAnswer);
        submissionRepository.save(submission);

        submission.pullDomainEvents().forEach(eventPublisher::publishEvent);

        return new SubmitAnswerResult(submission.isCorrect(), exercise.correctAnswer(), submission.feedback());
    }

    public record SubmitAnswerResult(boolean correct, String correctAnswer, String feedback) {}
}

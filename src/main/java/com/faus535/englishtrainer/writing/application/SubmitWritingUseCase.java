package com.faus535.englishtrainer.writing.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import com.faus535.englishtrainer.writing.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class SubmitWritingUseCase {

    private final WritingExerciseRepository exerciseRepository;
    private final WritingSubmissionRepository submissionRepository;
    private final WritingEvaluatorPort evaluator;

    public SubmitWritingUseCase(WritingExerciseRepository exerciseRepository,
                                 WritingSubmissionRepository submissionRepository,
                                 WritingEvaluatorPort evaluator) {
        this.exerciseRepository = exerciseRepository;
        this.submissionRepository = submissionRepository;
        this.evaluator = evaluator;
    }

    @Transactional
    public WritingSubmission execute(UUID userId, UUID exerciseIdValue, String text) throws NotFoundException {
        WritingExerciseId exerciseId = new WritingExerciseId(exerciseIdValue);
        WritingExercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Writing exercise not found: " + exerciseIdValue));

        WritingFeedback feedback;
        try {
            feedback = evaluator.evaluate(text, exercise.prompt(), exercise.level());
        } catch (Exception e) {
            feedback = WritingFeedback.empty();
        }

        WritingSubmission submission = WritingSubmission.create(userId, exerciseId, text, feedback);
        return submissionRepository.save(submission);
    }
}

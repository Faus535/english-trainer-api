package com.faus535.englishtrainer.reading.application;

import com.faus535.englishtrainer.reading.domain.*;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class SubmitReadingAnswersUseCase {

    private final ReadingPassageRepository passageRepository;
    private final ReadingSubmissionRepository submissionRepository;

    public SubmitReadingAnswersUseCase(ReadingPassageRepository passageRepository,
                                       ReadingSubmissionRepository submissionRepository) {
        this.passageRepository = passageRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional
    public SubmitResult execute(UUID userId, UUID passageIdValue, List<Integer> answers) throws NotFoundException {
        ReadingPassageId passageId = new ReadingPassageId(passageIdValue);
        ReadingPassage passage = passageRepository.findById(passageId)
                .orElseThrow(() -> new NotFoundException("Reading passage not found: " + passageIdValue));

        List<ReadingQuestion> questions = passage.questions();
        int correct = 0;
        for (int i = 0; i < Math.min(answers.size(), questions.size()); i++) {
            if (answers.get(i) == questions.get(i).correctAnswer()) {
                correct++;
            }
        }

        double score = questions.isEmpty() ? 0 : (double) correct / questions.size() * 100;

        ReadingSubmission submission = ReadingSubmission.create(userId, passageId, score, answers);
        submissionRepository.save(submission);

        return new SubmitResult(score, correct, questions.size());
    }

    public record SubmitResult(double score, int correctAnswers, int totalQuestions) {}
}

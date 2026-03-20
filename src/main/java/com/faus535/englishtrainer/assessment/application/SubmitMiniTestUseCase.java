package com.faus535.englishtrainer.assessment.application;

import com.faus535.englishtrainer.assessment.domain.MiniTestResult;
import com.faus535.englishtrainer.assessment.domain.MiniTestResultRepository;
import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@UseCase
public class SubmitMiniTestUseCase {

    private final MiniTestResultRepository miniTestResultRepository;
    private final GetMiniTestQuestionsUseCase getMiniTestQuestionsUseCase;

    public SubmitMiniTestUseCase(MiniTestResultRepository miniTestResultRepository,
                                 GetMiniTestQuestionsUseCase getMiniTestQuestionsUseCase) {
        this.miniTestResultRepository = miniTestResultRepository;
        this.getMiniTestQuestionsUseCase = getMiniTestQuestionsUseCase;
    }

    @Transactional
    public MiniTestResult execute(UserProfileId userId, String moduleName, String level, Map<String, String> answers) {
        List<TestQuestion> questions = getMiniTestQuestionsUseCase.execute(moduleName, level);

        int totalQuestions = questions.size();
        int correctAnswers = 0;
        for (TestQuestion q : questions) {
            String answer = answers.get(q.id());
            if (answer != null && answer.equals(q.correctAnswer())) {
                correctAnswers++;
            }
        }

        int score = totalQuestions > 0 ? (correctAnswers * 100) / totalQuestions : 0;

        MiniTestResult result = MiniTestResult.create(userId, moduleName, level, score, totalQuestions, correctAnswers);
        return miniTestResultRepository.save(result);
    }
}

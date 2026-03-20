package com.faus535.englishtrainer.assessment.application;

import com.faus535.englishtrainer.assessment.domain.LevelAssigner;
import com.faus535.englishtrainer.assessment.domain.LevelTestResult;
import com.faus535.englishtrainer.assessment.domain.LevelTestResultRepository;
import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserLevel;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UseCase
public class SubmitLevelTestUseCase {

    private final UserProfileRepository userProfileRepository;
    private final LevelTestResultRepository levelTestResultRepository;
    private final GetLevelTestQuestionsUseCase getLevelTestQuestionsUseCase;
    private final ApplicationEventPublisher eventPublisher;

    public SubmitLevelTestUseCase(UserProfileRepository userProfileRepository,
                                  LevelTestResultRepository levelTestResultRepository,
                                  GetLevelTestQuestionsUseCase getLevelTestQuestionsUseCase,
                                  ApplicationEventPublisher eventPublisher) {
        this.userProfileRepository = userProfileRepository;
        this.levelTestResultRepository = levelTestResultRepository;
        this.getLevelTestQuestionsUseCase = getLevelTestQuestionsUseCase;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public LevelTestResult execute(UserProfileId userId, Map<String, String> answers) throws UserProfileNotFoundException, InvalidModuleException {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(userId));

        List<TestQuestion> questions = getLevelTestQuestionsUseCase.execute();

        Map<String, List<TestQuestion>> questionsByType = new HashMap<>();
        for (TestQuestion q : questions) {
            questionsByType.computeIfAbsent(q.type(), k -> new java.util.ArrayList<>()).add(q);
        }

        int vocabScore = calculateSectionScore(questionsByType.getOrDefault("vocabulary", List.of()), answers);
        int grammarScore = calculateSectionScore(questionsByType.getOrDefault("grammar", List.of()), answers);
        int listeningScore = calculateSectionScore(questionsByType.getOrDefault("listening", List.of()), answers);
        int pronScore = calculateSectionScore(questionsByType.getOrDefault("pronunciation", List.of()), answers);

        Map<String, String> assignedLevels = new HashMap<>();
        assignedLevels.put("vocabulary", LevelAssigner.assignLevel(vocabScore));
        assignedLevels.put("grammar", LevelAssigner.assignLevel(grammarScore));
        assignedLevels.put("listening", LevelAssigner.assignLevel(listeningScore));
        assignedLevels.put("pronunciation", LevelAssigner.assignLevel(pronScore));
        assignedLevels.put("phrases", LevelAssigner.assignLevel((vocabScore + grammarScore) / 2));

        LevelTestResult result = LevelTestResult.create(userId, vocabScore, grammarScore, listeningScore, pronScore, assignedLevels);
        LevelTestResult saved = levelTestResultRepository.save(result);
        result.pullDomainEvents().forEach(eventPublisher::publishEvent);

        UserProfile updated = profile.markTestCompleted();
        for (Map.Entry<String, String> entry : assignedLevels.entrySet()) {
            updated = updated.updateModuleLevel(entry.getKey(), new UserLevel(entry.getValue()));
        }
        userProfileRepository.save(updated);

        return saved;
    }

    private int calculateSectionScore(List<TestQuestion> questions, Map<String, String> answers) {
        if (questions.isEmpty()) {
            return 0;
        }
        int correct = 0;
        for (TestQuestion q : questions) {
            String answer = answers.get(q.id());
            if (answer != null && answer.equals(q.correctAnswer())) {
                correct++;
            }
        }
        return (correct * 100) / questions.size();
    }
}

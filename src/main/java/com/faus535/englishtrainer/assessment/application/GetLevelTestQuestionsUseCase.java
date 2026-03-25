package com.faus535.englishtrainer.assessment.application;

import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import com.faus535.englishtrainer.assessment.domain.TestQuestionHistoryRepository;
import com.faus535.englishtrainer.assessment.domain.TestQuestionRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UseCase
public class GetLevelTestQuestionsUseCase {

    private final TestQuestionRepository testQuestionRepository;
    private final TestQuestionHistoryRepository historyRepository;

    private static final Map<String, Map<String, Integer>> QUESTION_DISTRIBUTION = Map.of(
            "vocabulary", Map.of("a1", 4, "a2", 3, "b1", 2, "b2", 2, "c1", 2, "c2", 2),
            "grammar", Map.of("a1", 4, "a2", 2, "b1", 2, "b2", 2, "c1", 1, "c2", 1),
            "listening", Map.of("a1", 3, "a2", 2, "b1", 2, "b2", 1, "c1", 1, "c2", 1),
            "pronunciation", Map.of("a1", 2, "a2", 2, "b1", 2, "b2", 1, "c1", 1, "c2", 1)
    );

    public GetLevelTestQuestionsUseCase(TestQuestionRepository testQuestionRepository,
                                         TestQuestionHistoryRepository historyRepository) {
        this.testQuestionRepository = testQuestionRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional(readOnly = true)
    public List<TestQuestion> execute(UserProfileId userId) {
        List<UUID> seenQuestionIds = historyRepository.findQuestionIdsByUser(userId);

        List<TestQuestion> questions = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> typeEntry : QUESTION_DISTRIBUTION.entrySet()) {
            String type = typeEntry.getKey();
            for (Map.Entry<String, Integer> levelEntry : typeEntry.getValue().entrySet()) {
                String level = levelEntry.getKey();
                int count = levelEntry.getValue();
                List<TestQuestion> selected = testQuestionRepository.findRandomByTypeAndLevelExcluding(
                        type, level, count, seenQuestionIds);
                questions.addAll(selected);
            }
        }
        return questions;
    }
}

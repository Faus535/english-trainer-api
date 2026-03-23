package com.faus535.englishtrainer.assessment.infrastructure;

import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import com.faus535.englishtrainer.assessment.domain.TestQuestionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class InMemoryTestQuestionRepository implements TestQuestionRepository {

    private final List<TestQuestion> questions = new ArrayList<>();

    public void addAll(List<TestQuestion> newQuestions) {
        questions.addAll(newQuestions);
    }

    @Override
    public List<TestQuestion> findRandomByTypeAndLevel(String type, String level, int count) {
        return questions.stream()
                .filter(q -> q.type().equals(type) && q.level().equals(level))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<TestQuestion> findRandomByTypeAndLevelExcluding(String type, String level, int count, List<UUID> excludeIds) {
        List<TestQuestion> result = questions.stream()
                .filter(q -> q.type().equals(type) && q.level().equals(level))
                .filter(q -> !excludeIds.contains(q.id()))
                .limit(count)
                .collect(Collectors.toList());
        if (result.size() < count) {
            return findRandomByTypeAndLevel(type, level, count);
        }
        return result;
    }

    @Override
    public List<TestQuestion> findById(UUID id) {
        return questions.stream()
                .filter(q -> q.id().equals(id))
                .toList();
    }
}

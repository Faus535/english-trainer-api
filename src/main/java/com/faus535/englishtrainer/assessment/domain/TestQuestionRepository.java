package com.faus535.englishtrainer.assessment.domain;

import java.util.List;
import java.util.UUID;

public interface TestQuestionRepository {

    List<TestQuestion> findRandomByTypeAndLevel(String type, String level, int count);

    List<TestQuestion> findRandomByTypeAndLevelExcluding(String type, String level, int count, List<UUID> excludeIds);

    List<TestQuestion> findById(UUID id);
}

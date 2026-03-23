package com.faus535.englishtrainer.assessment.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;
import java.util.UUID;

public interface TestQuestionHistoryRepository {

    List<UUID> findQuestionIdsByUser(UserProfileId userId);

    void saveAll(UserProfileId userId, List<UUID> questionIds);
}

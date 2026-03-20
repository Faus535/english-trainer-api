package com.faus535.englishtrainer.assessment.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

public interface LevelTestResultRepository {

    List<LevelTestResult> findByUser(UserProfileId userId);

    LevelTestResult save(LevelTestResult result);
}

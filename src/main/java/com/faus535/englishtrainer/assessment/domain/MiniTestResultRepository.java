package com.faus535.englishtrainer.assessment.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

public interface MiniTestResultRepository {

    List<MiniTestResult> findByUser(UserProfileId userId);

    List<MiniTestResult> findByUserAndModule(UserProfileId userId, String moduleName);

    MiniTestResult save(MiniTestResult result);
}

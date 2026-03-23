package com.faus535.englishtrainer.minimalpair.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

public interface MinimalPairResultRepository {

    MinimalPairResult save(MinimalPairResult result);

    List<MinimalPairResult> findByUserId(UserProfileId userId);

    List<CategoryAccuracy> countByUserIdAndCorrectGroupedByCategory(UserProfileId userId);

    record CategoryAccuracy(String soundCategory, long total, long correct) {
        public double accuracy() {
            return total == 0 ? 0.0 : (double) correct / total;
        }
    }
}

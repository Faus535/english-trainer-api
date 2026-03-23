package com.faus535.englishtrainer.minimalpair.domain;

import java.util.List;

public interface MinimalPairRepository {

    List<MinimalPair> findByLevelAndCategory(String level, String category, int limit);

    List<MinimalPair> findByLevel(String level, int limit);
}

package com.faus535.englishtrainer.minimalpair.infrastructure.persistence;

import com.faus535.englishtrainer.minimalpair.domain.MinimalPair;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
class JpaMinimalPairRepositoryAdapter implements MinimalPairRepository {

    private final JpaMinimalPairRepository jpaRepository;

    JpaMinimalPairRepositoryAdapter(JpaMinimalPairRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<MinimalPair> findByLevelAndCategory(String level, String category, int limit) {
        List<MinimalPair> pairs = jpaRepository.findBySoundCategoryAndLevel(category, level).stream()
                .map(MinimalPairEntity::toAggregate)
                .toList();
        return applyLimit(pairs, limit);
    }

    @Override
    public List<MinimalPair> findByLevel(String level, int limit) {
        List<MinimalPair> pairs = jpaRepository.findByLevel(level).stream()
                .map(MinimalPairEntity::toAggregate)
                .toList();
        return applyLimit(pairs, limit);
    }

    private List<MinimalPair> applyLimit(List<MinimalPair> pairs, int limit) {
        if (limit <= 0 || limit >= pairs.size()) {
            return pairs;
        }
        List<MinimalPair> mutable = new java.util.ArrayList<>(pairs);
        Collections.shuffle(mutable);
        return mutable.stream().limit(limit).toList();
    }
}

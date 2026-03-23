package com.faus535.englishtrainer.dailychallenge.infrastructure.persistence;

import com.faus535.englishtrainer.dailychallenge.domain.DailyChallenge;
import com.faus535.englishtrainer.dailychallenge.domain.DailyChallengeRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
class JpaDailyChallengeRepositoryAdapter implements DailyChallengeRepository {

    private final JpaDailyChallengeRepository jpaRepository;

    JpaDailyChallengeRepositoryAdapter(JpaDailyChallengeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<DailyChallenge> findByDate(LocalDate date) {
        return jpaRepository.findByChallengeDate(date)
                .map(DailyChallengeEntity::toAggregate);
    }

    @Override
    public DailyChallenge save(DailyChallenge dailyChallenge) {
        DailyChallengeEntity entity = DailyChallengeEntity.fromAggregate(dailyChallenge);
        if (jpaRepository.existsById(dailyChallenge.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}

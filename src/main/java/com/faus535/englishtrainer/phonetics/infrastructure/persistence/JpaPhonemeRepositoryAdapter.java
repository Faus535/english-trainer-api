package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaPhonemeRepositoryAdapter implements PhonemeRepository {
    private final JpaPhonemeRepository jpaRepository;

    JpaPhonemeRepositoryAdapter(JpaPhonemeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Phoneme> findAll() {
        return jpaRepository.findAllByOrderByDifficultyOrderAsc().stream()
                .map(PhonemeEntity::toAggregate).toList();
    }

    @Override
    public Optional<Phoneme> findById(PhonemeId id) {
        return jpaRepository.findById(id.value()).map(PhonemeEntity::toAggregate);
    }

    @Override
    public List<Phoneme> findUncompletedByUserOrderedByDifficulty(UUID userId) {
        return jpaRepository.findUncompletedByUserOrderedByDifficulty(userId).stream()
                .map(PhonemeEntity::toAggregate).toList();
    }
}

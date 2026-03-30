package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemeRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaPhonemeRepositoryAdapter implements PhonemeRepository {

    private final JpaPhonemeRepository jpaRepository;

    JpaPhonemeRepositoryAdapter(JpaPhonemeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Phoneme> findAll() {
        return jpaRepository.findAll().stream()
                .map(PhonemeEntity::toAggregate)
                .toList();
    }

    @Override
    public Optional<Phoneme> findById(PhonemeId id) {
        return jpaRepository.findById(id.value())
                .map(PhonemeEntity::toAggregate);
    }

    @Override
    public List<Phoneme> findUncompletedByUserOrderedByDifficulty(UserProfileId userId) {
        return jpaRepository.findUncompletedByUserOrderedByDifficulty(userId.value()).stream()
                .map(PhonemeEntity::toAggregate)
                .toList();
    }
}

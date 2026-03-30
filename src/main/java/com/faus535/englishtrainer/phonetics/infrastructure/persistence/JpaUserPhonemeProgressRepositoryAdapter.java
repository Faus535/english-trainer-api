package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.UserPhonemeProgressRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaUserPhonemeProgressRepositoryAdapter implements UserPhonemeProgressRepository {

    private final JpaUserPhonemeProgressRepository jpaRepository;

    JpaUserPhonemeProgressRepositoryAdapter(JpaUserPhonemeProgressRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UserPhonemeProgress> findByUserAndPhonemeAndPhrase(UserProfileId userId,
                                                                        PhonemeId phonemeId,
                                                                        PhonemePracticePhraseId phraseId) {
        return jpaRepository.findByUserIdAndPhonemeIdAndPhraseId(
                userId.value(), phonemeId.value(), phraseId.value()
        ).map(UserPhonemeProgressEntity::toAggregate);
    }

    @Override
    public List<UserPhonemeProgress> findByUserAndPhoneme(UserProfileId userId, PhonemeId phonemeId) {
        return jpaRepository.findByUserIdAndPhonemeId(userId.value(), phonemeId.value()).stream()
                .map(UserPhonemeProgressEntity::toAggregate)
                .toList();
    }

    @Override
    public UserPhonemeProgress save(UserPhonemeProgress progress) {
        UserPhonemeProgressEntity entity = UserPhonemeProgressEntity.fromAggregate(progress);
        if (jpaRepository.existsById(progress.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}

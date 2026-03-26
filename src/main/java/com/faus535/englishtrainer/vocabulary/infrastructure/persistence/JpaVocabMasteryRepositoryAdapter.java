package com.faus535.englishtrainer.vocabulary.infrastructure.persistence;

import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.MasterySource;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabMastery;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryRepository;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaVocabMasteryRepositoryAdapter implements VocabMasteryRepository {

    private final SpringDataVocabMasteryRepository jpaRepository;

    JpaVocabMasteryRepositoryAdapter(SpringDataVocabMasteryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<VocabMastery> findByUserIdAndVocabEntryId(UserProfileId userId, VocabEntryId vocabEntryId) {
        return jpaRepository.findByUserIdAndVocabEntryId(userId.value(), vocabEntryId.value())
                .map(this::toDomain);
    }

    @Override
    public Optional<VocabMastery> findByUserIdAndWord(UserProfileId userId, String word) {
        return jpaRepository.findByUserIdAndWord(userId.value(), word)
                .map(this::toDomain);
    }

    @Override
    public List<VocabMastery> findByUserId(UserProfileId userId) {
        return jpaRepository.findByUserId(userId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<VocabMastery> findByUserIdAndStatus(UserProfileId userId, VocabMasteryStatus status) {
        return jpaRepository.findByUserIdAndStatus(userId.value(), status.name())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public VocabMastery save(VocabMastery mastery) {
        VocabMasteryEntity entity = VocabMasteryEntity.fromAggregate(
                mastery.id().value(),
                mastery.userId().value(),
                mastery.vocabEntryId() != null ? mastery.vocabEntryId().value() : null,
                mastery.word(),
                mastery.correctCount(),
                mastery.incorrectCount(),
                mastery.totalAttempts(),
                mastery.status().name(),
                mastery.source().context(),
                mastery.source().detail(),
                mastery.lastPracticedAt(),
                mastery.learnedAt(),
                mastery.createdAt(),
                mastery.updatedAt()
        );
        if (jpaRepository.existsById(mastery.id().value())) {
            entity.markAsExisting();
        }
        VocabMasteryEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private VocabMastery toDomain(VocabMasteryEntity entity) {
        return VocabMastery.reconstitute(
                new VocabMasteryId(entity.getId()),
                new UserProfileId(entity.userId()),
                entity.vocabEntryId() != null ? new VocabEntryId(entity.vocabEntryId()) : null,
                entity.word(),
                entity.correctCount(),
                entity.incorrectCount(),
                entity.totalAttempts(),
                VocabMasteryStatus.valueOf(entity.status()),
                new MasterySource(entity.sourceContext(), entity.sourceDetail()),
                entity.lastPracticedAt(),
                entity.learnedAt(),
                entity.createdAt(),
                entity.updatedAt()
        );
    }
}

package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrill;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillId;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaPronunciationDrillRepositoryAdapter implements PronunciationDrillRepository {

    private final JpaPronunciationDrillRepository jpaRepository;

    JpaPronunciationDrillRepositoryAdapter(JpaPronunciationDrillRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<PronunciationDrill> findById(PronunciationDrillId id) {
        return jpaRepository.findById(id.value())
                .map(PronunciationDrillEntity::toAggregate);
    }

    @Override
    public List<PronunciationDrill> findByLevel(String cefrLevel) {
        return jpaRepository.findByCefrLevel(cefrLevel).stream()
                .map(PronunciationDrillEntity::toAggregate)
                .toList();
    }

    @Override
    public List<PronunciationDrill> findByLevelAndFocus(String cefrLevel, String focus) {
        return jpaRepository.findByCefrLevelAndFocus(cefrLevel, focus).stream()
                .map(PronunciationDrillEntity::toAggregate)
                .toList();
    }

    @Override
    public PronunciationDrill save(PronunciationDrill drill) {
        Optional<PronunciationDrillEntity> existing = jpaRepository.findById(drill.id().value());
        if (existing.isPresent()) {
            PronunciationDrillEntity entity = existing.get();
            entity.updateFrom(drill);
            return jpaRepository.save(entity).toAggregate();
        }
        PronunciationDrillEntity entity = PronunciationDrillEntity.fromAggregate(drill);
        return jpaRepository.save(entity).toAggregate();
    }
}

package com.faus535.englishtrainer.reading.infrastructure.persistence;

import com.faus535.englishtrainer.reading.domain.ReadingPassage;
import com.faus535.englishtrainer.reading.domain.ReadingPassageId;
import com.faus535.englishtrainer.reading.domain.ReadingPassageRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaReadingPassageRepositoryAdapter implements ReadingPassageRepository {

    private final JpaReadingPassageRepository jpaRepository;

    JpaReadingPassageRepositoryAdapter(JpaReadingPassageRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ReadingPassage> findByLevel(String level) {
        return jpaRepository.findByLevel(level).stream()
                .map(ReadingPassageEntity::toAggregate).toList();
    }

    @Override
    public Optional<ReadingPassage> findById(ReadingPassageId id) {
        return jpaRepository.findById(id.value()).map(ReadingPassageEntity::toAggregate);
    }

    @Override
    public ReadingPassage save(ReadingPassage passage) {
        ReadingPassageEntity entity = ReadingPassageEntity.fromAggregate(passage);
        if (jpaRepository.existsById(passage.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}

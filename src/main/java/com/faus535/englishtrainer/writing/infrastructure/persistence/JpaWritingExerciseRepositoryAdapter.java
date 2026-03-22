package com.faus535.englishtrainer.writing.infrastructure.persistence;

import com.faus535.englishtrainer.writing.domain.WritingExercise;
import com.faus535.englishtrainer.writing.domain.WritingExerciseId;
import com.faus535.englishtrainer.writing.domain.WritingExerciseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaWritingExerciseRepositoryAdapter implements WritingExerciseRepository {

    private final JpaWritingExerciseRepository jpaRepository;

    JpaWritingExerciseRepositoryAdapter(JpaWritingExerciseRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<WritingExercise> findByLevel(String level) {
        return jpaRepository.findByLevel(level).stream()
                .map(WritingExerciseEntity::toAggregate).toList();
    }

    @Override
    public Optional<WritingExercise> findById(WritingExerciseId id) {
        return jpaRepository.findById(id.value()).map(WritingExerciseEntity::toAggregate);
    }

    @Override
    public WritingExercise save(WritingExercise exercise) {
        WritingExerciseEntity entity = WritingExerciseEntity.fromAggregate(exercise);
        if (jpaRepository.existsById(exercise.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}

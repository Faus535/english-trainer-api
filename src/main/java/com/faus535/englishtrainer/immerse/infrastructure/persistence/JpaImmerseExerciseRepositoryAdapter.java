package com.faus535.englishtrainer.immerse.infrastructure.persistence;

import com.faus535.englishtrainer.immerse.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaImmerseExerciseRepositoryAdapter implements ImmerseExerciseRepository {

    private final JpaImmerseExerciseRepository jpaRepository;

    JpaImmerseExerciseRepositoryAdapter(JpaImmerseExerciseRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void saveAll(List<ImmerseExercise> exercises) {
        List<ImmerseExerciseEntity> entities = exercises.stream()
                .map(ImmerseExerciseEntity::fromDomain)
                .toList();
        jpaRepository.saveAll(entities);
    }

    @Override
    public List<ImmerseExercise> findByContentId(ImmerseContentId contentId) {
        return jpaRepository.findByContentIdOrderByOrderIndexAsc(contentId.value()).stream()
                .map(ImmerseExerciseEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<ImmerseExercise> findById(ImmerseExerciseId id) {
        return jpaRepository.findById(id.value()).map(ImmerseExerciseEntity::toDomain);
    }
}

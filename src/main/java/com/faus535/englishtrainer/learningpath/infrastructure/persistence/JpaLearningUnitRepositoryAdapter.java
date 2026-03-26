package com.faus535.englishtrainer.learningpath.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.learningpath.domain.LearningPathId;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitRepository;
import com.faus535.englishtrainer.learningpath.domain.MasteryScore;
import com.faus535.englishtrainer.learningpath.domain.UnitContent;
import com.faus535.englishtrainer.learningpath.domain.UnitStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaLearningUnitRepositoryAdapter implements LearningUnitRepository {

    private final SpringDataLearningUnitRepository jpaRepository;
    private final ObjectMapper objectMapper;

    JpaLearningUnitRepositoryAdapter(SpringDataLearningUnitRepository jpaRepository,
                                      ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<LearningUnit> findById(LearningUnitId id) {
        return jpaRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<LearningUnit> findByLearningPathId(LearningPathId learningPathId) {
        return jpaRepository.findByLearningPathIdOrderByUnitIndex(learningPathId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public LearningUnit save(LearningUnit learningUnit) {
        LearningUnitEntity entity = toEntity(learningUnit);
        if (jpaRepository.existsById(learningUnit.id().value())) {
            entity.markAsExisting();
        }
        jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public void saveAll(List<LearningUnit> learningUnits) {
        List<LearningUnitEntity> entities = learningUnits.stream()
                .map(unit -> {
                    LearningUnitEntity entity = toEntity(unit);
                    if (jpaRepository.existsById(unit.id().value())) {
                        entity.markAsExisting();
                    }
                    return entity;
                })
                .toList();
        jpaRepository.saveAll(entities);
    }

    private LearningUnitEntity toEntity(LearningUnit unit) {
        return LearningUnitEntity.fromAggregate(
                unit.id().value(),
                unit.learningPathId().value(),
                unit.unitIndex(),
                unit.unitName(),
                unit.targetLevel(),
                unit.status().name(),
                unit.masteryScore().value(),
                serializeContents(unit.contents()),
                unit.completedAt(),
                unit.createdAt(),
                unit.updatedAt()
        );
    }

    private LearningUnit toDomain(LearningUnitEntity entity) {
        return LearningUnit.reconstitute(
                new LearningUnitId(entity.getId()),
                new LearningPathId(entity.learningPathId()),
                entity.unitIndex(),
                entity.unitName(),
                entity.targetLevel(),
                UnitStatus.valueOf(entity.status()),
                new MasteryScore(entity.masteryScore()),
                deserializeContents(entity.contentsData()),
                entity.completedAt(),
                entity.createdAt(),
                entity.updatedAt()
        );
    }

    private String serializeContents(List<UnitContent> contents) {
        try {
            return objectMapper.writeValueAsString(contents);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize unit contents", e);
        }
    }

    private List<UnitContent> deserializeContents(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize unit contents", e);
        }
    }
}

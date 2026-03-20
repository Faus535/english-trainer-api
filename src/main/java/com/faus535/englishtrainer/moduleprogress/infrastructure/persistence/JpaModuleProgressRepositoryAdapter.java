package com.faus535.englishtrainer.moduleprogress.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgressId;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgressRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
class JpaModuleProgressRepositoryAdapter implements ModuleProgressRepository {

    private final JpaModuleProgressRepository jpaRepository;
    private final ObjectMapper objectMapper;

    JpaModuleProgressRepositoryAdapter(JpaModuleProgressRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<ModuleProgress> findByUserAndModuleAndLevel(UserProfileId userId, ModuleName moduleName, ModuleLevel level) {
        return jpaRepository.findByUserIdAndModuleNameAndLevel(userId.value(), moduleName.value(), level.value())
                .map(this::toDomain);
    }

    @Override
    public List<ModuleProgress> findAllByUser(UserProfileId userId) {
        return jpaRepository.findByUserId(userId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public ModuleProgress save(ModuleProgress progress) {
        ModuleProgressEntity entity = ModuleProgressEntity.fromAggregate(
                progress.id().value(),
                progress.userId().value(),
                progress.moduleName().value(),
                progress.level().value(),
                progress.currentUnit(),
                serializeCompletedUnits(progress.completedUnits()),
                serializeScores(progress.scores())
        );
        if (jpaRepository.existsById(progress.id().value())) {
            entity.markAsExisting();
        }
        ModuleProgressEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private ModuleProgress toDomain(ModuleProgressEntity entity) {
        List<Integer> completedUnits = deserializeCompletedUnits(entity.completedUnits());
        Map<Integer, Integer> scores = deserializeScores(entity.scores());

        return ModuleProgress.reconstitute(
                new ModuleProgressId(entity.getId()),
                new UserProfileId(entity.userId()),
                new ModuleName(entity.moduleName()),
                new ModuleLevel(entity.level()),
                entity.currentUnit(),
                completedUnits,
                scores
        );
    }

    private String serializeCompletedUnits(List<Integer> completedUnits) {
        try {
            return objectMapper.writeValueAsString(completedUnits);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize completedUnits", e);
        }
    }

    private List<Integer> deserializeCompletedUnits(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize completedUnits", e);
        }
    }

    private String serializeScores(Map<Integer, Integer> scores) {
        try {
            return objectMapper.writeValueAsString(scores);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize scores", e);
        }
    }

    private Map<Integer, Integer> deserializeScores(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize scores", e);
        }
    }
}

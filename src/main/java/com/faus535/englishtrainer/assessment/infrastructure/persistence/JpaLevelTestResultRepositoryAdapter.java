package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.assessment.domain.LevelTestResult;
import com.faus535.englishtrainer.assessment.domain.LevelTestResultId;
import com.faus535.englishtrainer.assessment.domain.LevelTestResultRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
class JpaLevelTestResultRepositoryAdapter implements LevelTestResultRepository {

    private final JpaLevelTestResultRepository jpaRepository;
    private final ObjectMapper objectMapper;

    JpaLevelTestResultRepositoryAdapter(JpaLevelTestResultRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<LevelTestResult> findByUser(UserProfileId userId) {
        return jpaRepository.findByUserId(userId.value())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public LevelTestResult save(LevelTestResult result) {
        String assignedLevelsJson = serializeAssignedLevels(result.assignedLevels());
        LevelTestResultEntity entity = LevelTestResultEntity.fromAggregate(
                result.id().value(),
                result.userId().value(),
                result.vocabularyScore(),
                result.grammarScore(),
                result.listeningScore(),
                result.pronunciationScore(),
                assignedLevelsJson,
                result.completedAt()
        );
        if (jpaRepository.existsById(result.id().value())) {
            entity.markAsExisting();
        }
        LevelTestResultEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private LevelTestResult toDomain(LevelTestResultEntity entity) {
        Map<String, String> assignedLevels = deserializeAssignedLevels(entity.assignedLevels());
        return LevelTestResult.reconstitute(
                new LevelTestResultId(entity.getId()),
                new UserProfileId(entity.userId()),
                entity.vocabularyScore(),
                entity.grammarScore(),
                entity.listeningScore(),
                entity.pronunciationScore(),
                assignedLevels,
                entity.completedAt()
        );
    }

    private String serializeAssignedLevels(Map<String, String> assignedLevels) {
        try {
            return objectMapper.writeValueAsString(assignedLevels);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize assigned levels", e);
        }
    }

    private Map<String, String> deserializeAssignedLevels(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize assigned levels", e);
        }
    }
}

package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import com.faus535.englishtrainer.assessment.domain.TestQuestionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class JpaTestQuestionRepositoryAdapter implements TestQuestionRepository {

    private final JpaTestQuestionRepository jpaRepository;
    private final ObjectMapper objectMapper;

    JpaTestQuestionRepositoryAdapter(JpaTestQuestionRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<TestQuestion> findRandomByTypeAndLevel(String type, String level, int count) {
        return jpaRepository.findRandomByTypeAndLevel(type, level, count).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<TestQuestion> findRandomByTypeAndLevelExcluding(String type, String level, int count, List<UUID> excludeIds) {
        if (excludeIds.isEmpty()) {
            return findRandomByTypeAndLevel(type, level, count);
        }
        List<TestQuestion> result = jpaRepository.findRandomByTypeAndLevelExcluding(type, level, count, excludeIds).stream()
                .map(this::toDomain)
                .toList();
        if (result.size() < count) {
            return jpaRepository.findRandomByTypeAndLevel(type, level, count).stream()
                    .map(this::toDomain)
                    .toList();
        }
        return result;
    }

    @Override
    public List<TestQuestion> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain)
                .stream()
                .toList();
    }

    private TestQuestion toDomain(TestQuestionEntity entity) {
        List<String> options = deserializeOptions(entity.options());
        return new TestQuestion(entity.getId(), entity.type(), entity.question(), options,
                entity.correctAnswer(), entity.level(), entity.audioSpeed());
    }

    private List<String> deserializeOptions(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize test question options", e);
        }
    }
}

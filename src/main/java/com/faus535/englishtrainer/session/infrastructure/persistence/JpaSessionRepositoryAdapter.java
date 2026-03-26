package com.faus535.englishtrainer.session.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionBlock;
import com.faus535.englishtrainer.session.domain.SessionExercise;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.SessionMode;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.session.domain.SessionType;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaSessionRepositoryAdapter implements SessionRepository {

    private final JpaSessionRepository jpaRepository;
    private final ObjectMapper objectMapper;

    JpaSessionRepositoryAdapter(JpaSessionRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Session> findById(SessionId id) {
        return jpaRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public Optional<Session> findActiveByUser(UserProfileId userId) {
        return jpaRepository.findByUserIdAndCompletedFalse(userId.value())
                .map(this::toDomain);
    }

    @Override
    public List<Session> findByUser(UserProfileId userId) {
        return jpaRepository.findByUserIdOrderByStartedAtDesc(userId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Session save(Session session) {
        SessionEntity entity = SessionEntity.fromAggregate(
                session.id().value(),
                session.userId().value(),
                session.mode().value(),
                session.sessionType().value(),
                session.listeningModule(),
                session.secondaryModule(),
                session.integratorTheme(),
                session.completed(),
                session.startedAt(),
                session.completedAt(),
                session.durationMinutes(),
                serializeBlocks(session.blocks()),
                serializeExercises(session.exercises())
        );
        if (jpaRepository.existsById(session.id().value())) {
            entity.markAsExisting();
        }
        SessionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private Session toDomain(SessionEntity entity) {
        List<SessionBlock> blocks = deserializeBlocks(entity.blocksData());
        List<SessionExercise> exercises = deserializeExercises(entity.exercisesData());
        return Session.reconstitute(
                new SessionId(entity.getId()),
                new UserProfileId(entity.userId()),
                new SessionMode(entity.mode()),
                new SessionType(entity.sessionType()),
                entity.listeningModule(),
                entity.secondaryModule(),
                entity.integratorTheme(),
                blocks,
                exercises,
                entity.completed(),
                entity.startedAt(),
                entity.completedAt(),
                entity.durationMinutes()
        );
    }

    private String serializeBlocks(List<SessionBlock> blocks) {
        try {
            return objectMapper.writeValueAsString(blocks);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize session blocks", e);
        }
    }

    private List<SessionBlock> deserializeBlocks(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize session blocks", e);
        }
    }

    private String serializeExercises(List<SessionExercise> exercises) {
        if (exercises == null || exercises.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(exercises);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize session exercises", e);
        }
    }

    private List<SessionExercise> deserializeExercises(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize session exercises", e);
        }
    }
}

package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "test_question_history")
class TestQuestionHistoryEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(name = "asked_at", nullable = false)
    private Instant askedAt;

    protected TestQuestionHistoryEntity() {}

    static TestQuestionHistoryEntity create(UUID userId, UUID questionId) {
        TestQuestionHistoryEntity entity = new TestQuestionHistoryEntity();
        entity.id = UUID.randomUUID();
        entity.userId = userId;
        entity.questionId = questionId;
        entity.askedAt = Instant.now();
        return entity;
    }

    UUID questionId() { return questionId; }
}

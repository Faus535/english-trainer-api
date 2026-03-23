package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "test_questions")
class TestQuestionEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "JSONB")
    private String options;

    @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(name = "audio_speed", columnDefinition = "NUMERIC(3,2)")
    private Double audioSpeed;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TestQuestionEntity() {}

    UUID getId() { return id; }
    String type() { return type; }
    String level() { return level; }
    String question() { return question; }
    String options() { return options; }
    String correctAnswer() { return correctAnswer; }
    Double audioSpeed() { return audioSpeed; }
}

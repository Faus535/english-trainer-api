package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import com.faus535.englishtrainer.assessment.domain.MiniTestResult;
import com.faus535.englishtrainer.assessment.domain.MiniTestResultId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mini_test_results")
class MiniTestResultEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "module_name", nullable = false, length = 50)
    private String moduleName;

    @Column(nullable = false, length = 10)
    private String level;

    @Column(nullable = false)
    private int score;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers;

    @Column(nullable = false, length = 20)
    private String recommendation;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    protected MiniTestResultEntity() {}

    static MiniTestResultEntity fromAggregate(MiniTestResult aggregate) {
        MiniTestResultEntity entity = new MiniTestResultEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId().value();
        entity.moduleName = aggregate.moduleName();
        entity.level = aggregate.level();
        entity.score = aggregate.score();
        entity.totalQuestions = aggregate.totalQuestions();
        entity.correctAnswers = aggregate.correctAnswers();
        entity.recommendation = aggregate.recommendation();
        entity.completedAt = aggregate.completedAt();
        return entity;
    }

    MiniTestResult toAggregate() {
        return MiniTestResult.reconstitute(
                new MiniTestResultId(id),
                new UserProfileId(userId),
                moduleName,
                level,
                score,
                totalQuestions,
                correctAnswers,
                recommendation,
                completedAt
        );
    }

    void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

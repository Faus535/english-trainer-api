package com.faus535.englishtrainer.immerse.infrastructure.persistence;

import com.faus535.englishtrainer.immerse.domain.*;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "immerse_submissions")
class ImmerseSubmissionEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(name = "exercise_id", nullable = false)
    private UUID exerciseId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_answer", nullable = false, columnDefinition = "TEXT")
    private String userAnswer;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    @Transient
    private boolean isNew = true;

    protected ImmerseSubmissionEntity() {}

    static ImmerseSubmissionEntity fromDomain(ImmerseSubmission submission) {
        ImmerseSubmissionEntity entity = new ImmerseSubmissionEntity();
        entity.id = submission.id().value();
        entity.exerciseId = submission.exerciseId().value();
        entity.userId = submission.userId();
        entity.userAnswer = submission.userAnswer();
        entity.correct = submission.isCorrect();
        entity.feedback = submission.feedback();
        entity.submittedAt = submission.submittedAt();
        return entity;
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

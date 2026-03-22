package com.faus535.englishtrainer.reading.infrastructure.persistence;

import com.faus535.englishtrainer.reading.domain.ReadingPassageId;
import com.faus535.englishtrainer.reading.domain.ReadingSubmission;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reading_submissions")
class ReadingSubmissionEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "passage_id", nullable = false)
    private UUID passageId;

    @Column(nullable = false)
    private double score;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answers;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    protected ReadingSubmissionEntity() {}

    static ReadingSubmissionEntity fromAggregate(ReadingSubmission submission) {
        ReadingSubmissionEntity entity = new ReadingSubmissionEntity();
        entity.id = submission.id();
        entity.isNew = true;
        entity.userId = submission.userId();
        entity.passageId = submission.passageId().value();
        entity.score = submission.score();
        entity.answers = submission.answers().stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
        entity.completedAt = submission.completedAt();
        return entity;
    }

    ReadingSubmission toDomain() {
        List<Integer> answerList = answers.isEmpty() ? List.of() :
                Arrays.stream(answers.split(",")).map(Integer::parseInt).toList();
        return ReadingSubmission.reconstitute(id, userId, new ReadingPassageId(passageId),
                score, answerList, completedAt);
    }

    void markAsExisting() { this.isNew = false; }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

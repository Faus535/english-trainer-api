package com.faus535.englishtrainer.writing.infrastructure.persistence;

import com.faus535.englishtrainer.writing.domain.WritingExerciseId;
import com.faus535.englishtrainer.writing.domain.WritingFeedback;
import com.faus535.englishtrainer.writing.domain.WritingSubmission;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "writing_submissions")
class WritingSubmissionEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "exercise_id", nullable = false)
    private UUID exerciseId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "word_count", nullable = false)
    private int wordCount;

    @Column(name = "grammar_score")
    private Double grammarScore;

    @Column(name = "coherence_score")
    private Double coherenceScore;

    @Column(name = "vocabulary_score")
    private Double vocabularyScore;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(columnDefinition = "TEXT")
    private String corrections;

    @Column(name = "level_assessment")
    private String levelAssessment;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    protected WritingSubmissionEntity() {}

    static WritingSubmissionEntity fromAggregate(WritingSubmission submission) {
        WritingSubmissionEntity entity = new WritingSubmissionEntity();
        entity.id = submission.id();
        entity.isNew = true;
        entity.userId = submission.userId();
        entity.exerciseId = submission.exerciseId().value();
        entity.text = submission.text();
        entity.wordCount = submission.wordCount();
        WritingFeedback fb = submission.feedback();
        if (fb != null) {
            entity.grammarScore = fb.grammarScore();
            entity.coherenceScore = fb.coherenceScore();
            entity.vocabularyScore = fb.vocabularyScore();
            entity.overallScore = fb.overallScore();
            entity.levelAssessment = fb.levelAssessment();
            entity.feedback = fb.generalFeedback();
            entity.corrections = String.join("|||", fb.corrections());
        }
        entity.submittedAt = submission.submittedAt();
        return entity;
    }

    WritingSubmission toDomain() {
        List<String> correctionsList = corrections != null && !corrections.isEmpty()
                ? Arrays.asList(corrections.split("\\|\\|\\|")) : List.of();
        WritingFeedback fb = new WritingFeedback(
                grammarScore != null ? grammarScore : 0,
                coherenceScore != null ? coherenceScore : 0,
                vocabularyScore != null ? vocabularyScore : 0,
                overallScore != null ? overallScore : 0,
                levelAssessment, feedback, correctionsList);
        return WritingSubmission.reconstitute(id, userId, new WritingExerciseId(exerciseId),
                text, wordCount, fb, submittedAt);
    }

    void markAsExisting() { this.isNew = false; }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

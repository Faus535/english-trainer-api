package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.article.domain.ArticleQuestionAnswer;
import com.faus535.englishtrainer.article.domain.ArticleQuestionAnswerId;
import com.faus535.englishtrainer.article.domain.ArticleQuestionId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "article_question_answers")
class ArticleQuestionAnswerEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    private boolean isNew;

    @Column(name = "article_question_id", nullable = false)
    private UUID articleQuestionId;

    @Column(name = "user_answer", columnDefinition = "TEXT", nullable = false)
    private String userAnswer;

    @Column(name = "is_content_correct")
    private Boolean isContentCorrect;

    @Column(name = "grammar_feedback", columnDefinition = "TEXT")
    private String grammarFeedback;

    @Column(name = "style_feedback", columnDefinition = "TEXT")
    private String styleFeedback;

    @Column(name = "correction_summary", columnDefinition = "TEXT")
    private String correctionSummary;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ArticleQuestionAnswerEntity() {}

    static ArticleQuestionAnswerEntity fromDomain(ArticleQuestionAnswer answer) {
        ArticleQuestionAnswerEntity entity = new ArticleQuestionAnswerEntity();
        entity.id = answer.id().value();
        entity.isNew = true;
        entity.articleQuestionId = answer.questionId().value();
        entity.userAnswer = answer.userAnswer();
        entity.isContentCorrect = answer.isContentCorrect();
        entity.grammarFeedback = answer.grammarFeedback();
        entity.styleFeedback = answer.styleFeedback();
        entity.correctionSummary = answer.correctionSummary();
        entity.createdAt = answer.createdAt();
        return entity;
    }

    ArticleQuestionAnswer toDomain() {
        return ArticleQuestionAnswer.reconstitute(
                new ArticleQuestionAnswerId(id),
                new ArticleQuestionId(articleQuestionId),
                userAnswer, isContentCorrect != null && isContentCorrect,
                grammarFeedback, styleFeedback, correctionSummary, createdAt);
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.article.domain.ArticleQuestion;
import com.faus535.englishtrainer.article.domain.ArticleQuestionId;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "article_questions")
class ArticleQuestionEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    private boolean isNew;

    @Column(name = "article_reading_id", nullable = false)
    private UUID articleReadingId;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(name = "min_words", nullable = false)
    private int minWords;

    @Column(name = "hint_text", columnDefinition = "TEXT")
    private String hintText;

    protected ArticleQuestionEntity() {}

    static ArticleQuestionEntity fromDomain(ArticleQuestion question) {
        ArticleQuestionEntity entity = new ArticleQuestionEntity();
        entity.id = question.id().value();
        entity.isNew = true;
        entity.articleReadingId = question.articleReadingId().value();
        entity.questionText = question.questionText();
        entity.orderIndex = question.orderIndex();
        entity.minWords = question.minWords();
        entity.hintText = question.hintText();
        return entity;
    }

    ArticleQuestion toDomain() {
        return ArticleQuestion.reconstitute(
                new ArticleQuestionId(id),
                new ArticleReadingId(articleReadingId),
                questionText, orderIndex, minWords, hintText);
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

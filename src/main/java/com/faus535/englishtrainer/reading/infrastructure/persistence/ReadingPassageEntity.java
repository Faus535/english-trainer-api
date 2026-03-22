package com.faus535.englishtrainer.reading.infrastructure.persistence;

import com.faus535.englishtrainer.reading.domain.ReadingPassage;
import com.faus535.englishtrainer.reading.domain.ReadingPassageId;
import com.faus535.englishtrainer.reading.domain.ReadingQuestion;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reading_passages")
class ReadingPassageEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String level;

    private String topic;

    @Column(name = "word_count")
    private int wordCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "passage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ReadingQuestionEntity> questions = new ArrayList<>();

    protected ReadingPassageEntity() {}

    static ReadingPassageEntity fromAggregate(ReadingPassage aggregate) {
        ReadingPassageEntity entity = new ReadingPassageEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.title = aggregate.title();
        entity.content = aggregate.content();
        entity.level = aggregate.level();
        entity.topic = aggregate.topic();
        entity.wordCount = aggregate.wordCount();
        entity.createdAt = aggregate.createdAt();
        entity.questions = aggregate.questions().stream()
                .map(q -> ReadingQuestionEntity.fromDomain(q, entity))
                .toList();
        return entity;
    }

    ReadingPassage toAggregate() {
        List<ReadingQuestion> domainQuestions = questions.stream()
                .map(ReadingQuestionEntity::toDomain)
                .toList();
        return ReadingPassage.reconstitute(
                new ReadingPassageId(id), title, content, level, topic,
                wordCount, createdAt, domainQuestions);
    }

    void markAsExisting() { this.isNew = false; }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

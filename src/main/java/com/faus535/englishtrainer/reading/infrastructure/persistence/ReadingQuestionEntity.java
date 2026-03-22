package com.faus535.englishtrainer.reading.infrastructure.persistence;

import com.faus535.englishtrainer.reading.domain.ReadingQuestion;
import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reading_questions")
class ReadingQuestionEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passage_id", nullable = false)
    private ReadingPassageEntity passage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String options;

    @Column(name = "correct_answer", nullable = false)
    private int correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    protected ReadingQuestionEntity() {}

    static ReadingQuestionEntity fromDomain(ReadingQuestion domain, ReadingPassageEntity passage) {
        ReadingQuestionEntity entity = new ReadingQuestionEntity();
        entity.id = domain.id();
        entity.passage = passage;
        entity.question = domain.question();
        entity.options = String.join("|||", domain.options());
        entity.correctAnswer = domain.correctAnswer();
        entity.explanation = domain.explanation();
        return entity;
    }

    ReadingQuestion toDomain() {
        List<String> optionsList = Arrays.asList(options.split("\\|\\|\\|"));
        return ReadingQuestion.reconstitute(id, question, optionsList, correctAnswer, explanation);
    }
}

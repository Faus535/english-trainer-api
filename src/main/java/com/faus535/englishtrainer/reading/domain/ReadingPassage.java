package com.faus535.englishtrainer.reading.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.List;

public final class ReadingPassage extends AggregateRoot<ReadingPassageId> {

    private final ReadingPassageId id;
    private final String title;
    private final String content;
    private final String level;
    private final String topic;
    private final int wordCount;
    private final Instant createdAt;
    private final List<ReadingQuestion> questions;

    private ReadingPassage(ReadingPassageId id, String title, String content, String level,
                           String topic, int wordCount, Instant createdAt, List<ReadingQuestion> questions) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.level = level;
        this.topic = topic;
        this.wordCount = wordCount;
        this.createdAt = createdAt;
        this.questions = List.copyOf(questions);
    }

    public static ReadingPassage create(String title, String content, String level, String topic,
                                         List<ReadingQuestion> questions) {
        int wordCount = content.split("\\s+").length;
        return new ReadingPassage(ReadingPassageId.generate(), title, content, level, topic,
                wordCount, Instant.now(), questions);
    }

    public static ReadingPassage reconstitute(ReadingPassageId id, String title, String content,
                                               String level, String topic, int wordCount,
                                               Instant createdAt, List<ReadingQuestion> questions) {
        return new ReadingPassage(id, title, content, level, topic, wordCount, createdAt, questions);
    }

    public ReadingPassageId id() { return id; }
    public String title() { return title; }
    public String content() { return content; }
    public String level() { return level; }
    public String topic() { return topic; }
    public int wordCount() { return wordCount; }
    public Instant createdAt() { return createdAt; }
    public List<ReadingQuestion> questions() { return questions; }
}

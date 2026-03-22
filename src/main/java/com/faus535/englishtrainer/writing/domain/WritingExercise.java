package com.faus535.englishtrainer.writing.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;

public final class WritingExercise extends AggregateRoot<WritingExerciseId> {

    private final WritingExerciseId id;
    private final String prompt;
    private final String level;
    private final String topic;
    private final int minWords;
    private final int maxWords;
    private final Instant createdAt;

    private WritingExercise(WritingExerciseId id, String prompt, String level, String topic,
                            int minWords, int maxWords, Instant createdAt) {
        this.id = id;
        this.prompt = prompt;
        this.level = level;
        this.topic = topic;
        this.minWords = minWords;
        this.maxWords = maxWords;
        this.createdAt = createdAt;
    }

    public static WritingExercise create(String prompt, String level, String topic, int minWords, int maxWords) {
        return new WritingExercise(WritingExerciseId.generate(), prompt, level, topic, minWords, maxWords, Instant.now());
    }

    public static WritingExercise reconstitute(WritingExerciseId id, String prompt, String level, String topic,
                                                int minWords, int maxWords, Instant createdAt) {
        return new WritingExercise(id, prompt, level, topic, minWords, maxWords, createdAt);
    }

    public WritingExerciseId id() { return id; }
    public String prompt() { return prompt; }
    public String level() { return level; }
    public String topic() { return topic; }
    public int minWords() { return minWords; }
    public int maxWords() { return maxWords; }
    public Instant createdAt() { return createdAt; }
}

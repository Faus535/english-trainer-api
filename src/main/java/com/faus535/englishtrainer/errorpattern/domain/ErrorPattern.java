package com.faus535.englishtrainer.errorpattern.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ErrorPattern {

    private final ErrorPatternId id;
    private final UUID userId;
    private final ErrorCategory category;
    private final String pattern;
    private final List<String> examples;
    private final int occurrenceCount;
    private final Instant lastOccurred;
    private final boolean resolved;
    private final Instant createdAt;

    private ErrorPattern(ErrorPatternId id, UUID userId, ErrorCategory category, String pattern,
                         List<String> examples, int occurrenceCount, Instant lastOccurred,
                         boolean resolved, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.pattern = pattern;
        this.examples = List.copyOf(examples);
        this.occurrenceCount = occurrenceCount;
        this.lastOccurred = lastOccurred;
        this.resolved = resolved;
        this.createdAt = createdAt;
    }

    public static ErrorPattern create(UUID userId, ErrorCategory category, String pattern, String example) {
        return new ErrorPattern(
                ErrorPatternId.generate(), userId, category, pattern,
                List.of(example), 1, Instant.now(), false, Instant.now());
    }

    public static ErrorPattern reconstitute(ErrorPatternId id, UUID userId, ErrorCategory category,
                                              String pattern, List<String> examples, int occurrenceCount,
                                              Instant lastOccurred, boolean resolved, Instant createdAt) {
        return new ErrorPattern(id, userId, category, pattern, examples, occurrenceCount,
                lastOccurred, resolved, createdAt);
    }

    public ErrorPattern recordOccurrence(String example) {
        List<String> newExamples = new ArrayList<>(examples);
        if (newExamples.size() >= 10) {
            newExamples.removeFirst();
        }
        newExamples.add(example);
        return new ErrorPattern(id, userId, category, pattern, newExamples,
                occurrenceCount + 1, Instant.now(), false, createdAt);
    }

    public ErrorPattern markResolved() {
        return new ErrorPattern(id, userId, category, pattern, examples,
                occurrenceCount, lastOccurred, true, createdAt);
    }

    public ErrorPatternId id() { return id; }
    public UUID userId() { return userId; }
    public ErrorCategory category() { return category; }
    public String pattern() { return pattern; }
    public List<String> examples() { return examples; }
    public int occurrenceCount() { return occurrenceCount; }
    public Instant lastOccurred() { return lastOccurred; }
    public boolean resolved() { return resolved; }
    public Instant createdAt() { return createdAt; }
}

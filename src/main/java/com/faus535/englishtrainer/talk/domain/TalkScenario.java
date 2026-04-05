package com.faus535.englishtrainer.talk.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;

public final class TalkScenario extends AggregateRoot<TalkScenarioId> {

    private final TalkScenarioId id;
    private final String title;
    private final String description;
    private final String contextPrompt;
    private final String category;
    private final TalkLevel cefrLevel;
    private final int difficultyOrder;
    private final Instant createdAt;

    private TalkScenario(TalkScenarioId id, String title, String description, String contextPrompt,
                         String category, TalkLevel cefrLevel, int difficultyOrder, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.contextPrompt = contextPrompt;
        this.category = category;
        this.cefrLevel = cefrLevel;
        this.difficultyOrder = difficultyOrder;
        this.createdAt = createdAt;
    }

    public static TalkScenario reconstitute(TalkScenarioId id, String title, String description,
                                             String contextPrompt, String category, TalkLevel cefrLevel,
                                             int difficultyOrder, Instant createdAt) {
        return new TalkScenario(id, title, description, contextPrompt, category, cefrLevel,
                difficultyOrder, createdAt);
    }

    public TalkScenarioId id() { return id; }
    public String title() { return title; }
    public String description() { return description; }
    public String contextPrompt() { return contextPrompt; }
    public String category() { return category; }
    public TalkLevel cefrLevel() { return cefrLevel; }
    public int difficultyOrder() { return difficultyOrder; }
    public Instant createdAt() { return createdAt; }
}

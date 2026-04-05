package com.faus535.englishtrainer.talk.infrastructure.persistence;

import com.faus535.englishtrainer.talk.domain.TalkLevel;
import com.faus535.englishtrainer.talk.domain.TalkScenario;
import com.faus535.englishtrainer.talk.domain.TalkScenarioId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "talk_scenarios")
class TalkScenarioEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "context_prompt", nullable = false, columnDefinition = "TEXT")
    private String contextPrompt;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "cefr_level", nullable = false, length = 5)
    private String cefrLevel;

    @Column(name = "difficulty_order", nullable = false)
    private int difficultyOrder;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TalkScenarioEntity() {}

    TalkScenario toAggregate() {
        return TalkScenario.reconstitute(
                new TalkScenarioId(id), title, description, contextPrompt,
                category, new TalkLevel(cefrLevel), difficultyOrder, createdAt);
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return false; }

    String getCefrLevel() { return cefrLevel; }
}

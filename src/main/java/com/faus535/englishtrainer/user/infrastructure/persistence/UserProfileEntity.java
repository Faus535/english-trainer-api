package com.faus535.englishtrainer.user.infrastructure.persistence;

import com.faus535.englishtrainer.user.domain.UserLevel;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
class UserProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "test_completed", nullable = false)
    private boolean testCompleted;

    @Column(name = "level_listening", nullable = false)
    private String levelListening;

    @Column(name = "level_vocabulary", nullable = false)
    private String levelVocabulary;

    @Column(name = "level_grammar", nullable = false)
    private String levelGrammar;

    @Column(name = "level_phrases", nullable = false)
    private String levelPhrases;

    @Column(name = "level_pronunciation", nullable = false)
    private String levelPronunciation;

    @Column(name = "session_count", nullable = false)
    private int sessionCount;

    @Column(name = "sessions_this_week", nullable = false)
    private int sessionsThisWeek;

    @Column(name = "week_start")
    private LocalDate weekStart;

    @Column(nullable = false)
    private int xp;

    @Column(name = "last_test_at")
    private Instant lastTestAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserProfileEntity() {}

    static UserProfileEntity fromAggregate(UserProfile aggregate) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.id = aggregate.id().value();
        entity.version = aggregate.version();
        entity.isNew = aggregate.version() == null;
        entity.testCompleted = aggregate.testCompleted();
        entity.levelListening = aggregate.levelListening().value();
        entity.levelVocabulary = aggregate.levelVocabulary().value();
        entity.levelGrammar = aggregate.levelGrammar().value();
        entity.levelPhrases = aggregate.levelPhrases().value();
        entity.levelPronunciation = aggregate.levelPronunciation().value();
        entity.sessionCount = aggregate.sessionCount();
        entity.sessionsThisWeek = aggregate.sessionsThisWeek();
        entity.weekStart = aggregate.weekStart();
        entity.xp = aggregate.xp();
        entity.lastTestAt = aggregate.lastTestAt();
        entity.createdAt = aggregate.createdAt();
        entity.updatedAt = aggregate.updatedAt();
        return entity;
    }

    UserProfile toAggregate() {
        return UserProfile.reconstitute(
                new UserProfileId(id),
                version,
                testCompleted,
                new UserLevel(levelListening),
                new UserLevel(levelVocabulary),
                new UserLevel(levelGrammar),
                new UserLevel(levelPhrases),
                new UserLevel(levelPronunciation),
                sessionCount,
                sessionsThisWeek,
                weekStart,
                xp,
                lastTestAt,
                createdAt,
                updatedAt
        );
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

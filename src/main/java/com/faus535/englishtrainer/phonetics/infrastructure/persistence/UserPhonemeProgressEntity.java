package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_phoneme_progress")
class UserPhonemeProgressEntity implements Persistable<UUID> {
    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "phoneme_id", nullable = false)
    private UUID phonemeId;

    @Column(name = "phrase_id", nullable = false)
    private UUID phraseId;

    @Column(name = "attempts_count", nullable = false)
    private int attemptsCount;

    @Column(name = "correct_attempts_count", nullable = false)
    private int correctAttemptsCount;

    @Column(name = "best_score", nullable = false)
    private int bestScore;

    @Column(name = "phrase_completed", nullable = false)
    private boolean phraseCompleted;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    protected UserPhonemeProgressEntity() {}

    static UserPhonemeProgressEntity fromAggregate(UserPhonemeProgress agg, boolean isNewEntity) {
        UserPhonemeProgressEntity e = new UserPhonemeProgressEntity();
        e.id = agg.id().value();
        e.isNew = isNewEntity;
        e.userId = agg.userId().value();
        e.phonemeId = agg.phonemeId().value();
        e.phraseId = agg.phraseId().value();
        e.attemptsCount = agg.attemptsCount();
        e.correctAttemptsCount = agg.correctAttemptsCount();
        e.bestScore = agg.bestScore();
        e.phraseCompleted = agg.phraseCompleted();
        e.lastAttemptAt = agg.lastAttemptAt();
        return e;
    }

    UserPhonemeProgress toAggregate() {
        return UserPhonemeProgress.reconstitute(
            new UserPhonemeProgressId(id), new UserProfileId(userId),
            new PhonemeId(phonemeId), new PhonemePracticePhraseId(phraseId),
            attemptsCount, correctAttemptsCount, bestScore, phraseCompleted, lastAttemptAt
        );
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

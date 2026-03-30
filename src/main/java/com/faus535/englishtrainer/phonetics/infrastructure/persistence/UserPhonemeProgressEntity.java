package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhraseId;
import com.faus535.englishtrainer.phonetics.domain.UserPhonemeProgress;
import com.faus535.englishtrainer.phonetics.domain.UserPhonemeProgressId;
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

    @Column(nullable = false)
    private boolean correct;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;

    protected UserPhonemeProgressEntity() {}

    static UserPhonemeProgressEntity fromAggregate(UserPhonemeProgress aggregate) {
        UserPhonemeProgressEntity entity = new UserPhonemeProgressEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId().value();
        entity.phonemeId = aggregate.phonemeId().value();
        entity.phraseId = aggregate.phraseId().value();
        entity.correct = aggregate.correct();
        entity.attemptedAt = aggregate.attemptedAt();
        return entity;
    }

    UserPhonemeProgress toAggregate() {
        return UserPhonemeProgress.reconstitute(
                new UserPhonemeProgressId(id),
                new UserProfileId(userId),
                new PhonemeId(phonemeId),
                new PhonemePracticePhraseId(phraseId),
                correct, attemptedAt
        );
    }

    void markAsExisting() { this.isNew = false; }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}

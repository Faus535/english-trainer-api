package com.faus535.englishtrainer.user.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;
import com.faus535.englishtrainer.user.domain.event.UserProfileCreatedEvent;
import com.faus535.englishtrainer.user.domain.event.XpGrantedEvent;
import com.faus535.englishtrainer.user.domain.vo.EnglishLevel;

import java.time.Instant;

public final class UserProfile extends AggregateRoot<UserProfileId> {

    private final UserProfileId id;
    private final Long version;
    private final int xp;
    private final EnglishLevel englishLevel;
    private final Instant createdAt;
    private final Instant updatedAt;

    private UserProfile(UserProfileId id, Long version, int xp, EnglishLevel englishLevel,
                        Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.version = version;
        this.xp = xp;
        this.englishLevel = englishLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserProfile create() {
        Instant now = Instant.now();
        UserProfile profile = new UserProfile(UserProfileId.generate(), null, 0, null, now, now);
        profile.registerEvent(new UserProfileCreatedEvent(profile.id()));
        return profile;
    }

    public static UserProfile reconstitute(UserProfileId id, Long version, int xp, EnglishLevel englishLevel,
                                           Instant createdAt, Instant updatedAt) {
        return new UserProfile(id, version, xp, englishLevel, createdAt, updatedAt);
    }

    public UserProfile addXp(int amount) throws InvalidXpAmountException {
        if (amount < 0) {
            throw new InvalidXpAmountException(amount);
        }
        UserProfile updated = new UserProfile(id, version, xp + amount, englishLevel, createdAt, Instant.now());
        updated.registerEvent(new XpGrantedEvent(id, amount, xp + amount));
        return updated;
    }

    public UserProfile updateEnglishLevel(EnglishLevel level) {
        return new UserProfile(id, version, xp, level, createdAt, Instant.now());
    }

    public UserProfileId id() { return id; }
    public Long version() { return version; }
    public int xp() { return xp; }
    public EnglishLevel englishLevel() { return englishLevel; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}

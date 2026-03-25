package com.faus535.englishtrainer.user.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;
import com.faus535.englishtrainer.user.domain.event.UserProfileCreatedEvent;
import com.faus535.englishtrainer.user.domain.event.XpGrantedEvent;

import java.time.Instant;
import java.time.LocalDate;

public final class UserProfile extends AggregateRoot<UserProfileId> {

    private final UserProfileId id;
    private final Long version;
    private final boolean testCompleted;
    private final UserLevel levelListening;
    private final UserLevel levelVocabulary;
    private final UserLevel levelGrammar;
    private final UserLevel levelPhrases;
    private final UserLevel levelPronunciation;
    private final int sessionCount;
    private final int sessionsThisWeek;
    private final LocalDate weekStart;
    private final int xp;
    private final Instant lastTestAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    private UserProfile(UserProfileId id, Long version, boolean testCompleted, UserLevel levelListening,
                        UserLevel levelVocabulary, UserLevel levelGrammar, UserLevel levelPhrases,
                        UserLevel levelPronunciation, int sessionCount, int sessionsThisWeek,
                        LocalDate weekStart, int xp, Instant lastTestAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.version = version;
        this.testCompleted = testCompleted;
        this.levelListening = levelListening;
        this.levelVocabulary = levelVocabulary;
        this.levelGrammar = levelGrammar;
        this.levelPhrases = levelPhrases;
        this.levelPronunciation = levelPronunciation;
        this.sessionCount = sessionCount;
        this.sessionsThisWeek = sessionsThisWeek;
        this.weekStart = weekStart;
        this.xp = xp;
        this.lastTestAt = lastTestAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserProfile create() {
        UserLevel defaultLevel = UserLevel.defaultLevel();
        Instant now = Instant.now();
        UserProfile profile = new UserProfile(
                UserProfileId.generate(),
                null,
                false,
                defaultLevel,
                defaultLevel,
                defaultLevel,
                defaultLevel,
                defaultLevel,
                0,
                0,
                null,
                0,
                null,
                now,
                now
        );
        profile.registerEvent(new UserProfileCreatedEvent(profile.id()));
        return profile;
    }

    public static UserProfile reconstitute(UserProfileId id, Long version, boolean testCompleted, UserLevel levelListening,
                                           UserLevel levelVocabulary, UserLevel levelGrammar, UserLevel levelPhrases,
                                           UserLevel levelPronunciation, int sessionCount, int sessionsThisWeek,
                                           LocalDate weekStart, int xp, Instant lastTestAt, Instant createdAt, Instant updatedAt) {
        return new UserProfile(id, version, testCompleted, levelListening, levelVocabulary, levelGrammar,
                levelPhrases, levelPronunciation, sessionCount, sessionsThisWeek, weekStart, xp, lastTestAt, createdAt, updatedAt);
    }

    public UserProfile markTestCompleted() {
        return new UserProfile(id, version, true, levelListening, levelVocabulary, levelGrammar,
                levelPhrases, levelPronunciation, sessionCount, sessionsThisWeek, weekStart, xp, Instant.now(), createdAt, Instant.now());
    }

    public UserProfile resetTest() {
        return new UserProfile(id, version, false, levelListening, levelVocabulary, levelGrammar,
                levelPhrases, levelPronunciation, sessionCount, sessionsThisWeek, weekStart, xp, lastTestAt, createdAt, Instant.now());
    }

    public UserProfile updateModuleLevel(String module, UserLevel level) throws InvalidModuleException {
        return switch (module.toLowerCase()) {
            case "listening" -> new UserProfile(id, version, testCompleted, level, levelVocabulary, levelGrammar,
                    levelPhrases, levelPronunciation, sessionCount, sessionsThisWeek, weekStart, xp, lastTestAt, createdAt, Instant.now());
            case "vocabulary" -> new UserProfile(id, version, testCompleted, levelListening, level, levelGrammar,
                    levelPhrases, levelPronunciation, sessionCount, sessionsThisWeek, weekStart, xp, lastTestAt, createdAt, Instant.now());
            case "grammar" -> new UserProfile(id, version, testCompleted, levelListening, levelVocabulary, level,
                    levelPhrases, levelPronunciation, sessionCount, sessionsThisWeek, weekStart, xp, lastTestAt, createdAt, Instant.now());
            case "phrases" -> new UserProfile(id, version, testCompleted, levelListening, levelVocabulary, levelGrammar,
                    level, levelPronunciation, sessionCount, sessionsThisWeek, weekStart, xp, lastTestAt, createdAt, Instant.now());
            case "pronunciation" -> new UserProfile(id, version, testCompleted, levelListening, levelVocabulary, levelGrammar,
                    levelPhrases, level, sessionCount, sessionsThisWeek, weekStart, xp, lastTestAt, createdAt, Instant.now());
            default -> throw new InvalidModuleException(module);
        };
    }

    public UserProfile recordSession() {
        return new UserProfile(id, version, testCompleted, levelListening, levelVocabulary, levelGrammar,
                levelPhrases, levelPronunciation, sessionCount + 1, sessionsThisWeek + 1, weekStart, xp, lastTestAt, createdAt, Instant.now());
    }

    public UserProfile addXp(int amount) throws InvalidXpAmountException {
        if (amount < 0) {
            throw new InvalidXpAmountException(amount);
        }
        UserProfile updated = new UserProfile(id, version, testCompleted, levelListening, levelVocabulary, levelGrammar,
                levelPhrases, levelPronunciation, sessionCount, sessionsThisWeek, weekStart, xp + amount, lastTestAt, createdAt, Instant.now());
        updated.registerEvent(new XpGrantedEvent(id, amount, xp + amount));
        return updated;
    }

    public UserProfile resetWeeklyCounters() {
        return new UserProfile(id, version, testCompleted, levelListening, levelVocabulary, levelGrammar,
                levelPhrases, levelPronunciation, sessionCount, 0, LocalDate.now(), xp, lastTestAt, createdAt, Instant.now());
    }

    public UserProfileId id() { return id; }
    public Long version() { return version; }
    public boolean testCompleted() { return testCompleted; }
    public UserLevel levelListening() { return levelListening; }
    public UserLevel levelVocabulary() { return levelVocabulary; }
    public UserLevel levelGrammar() { return levelGrammar; }
    public UserLevel levelPhrases() { return levelPhrases; }
    public UserLevel levelPronunciation() { return levelPronunciation; }
    public int sessionCount() { return sessionCount; }
    public int sessionsThisWeek() { return sessionsThisWeek; }
    public LocalDate weekStart() { return weekStart; }
    public int xp() { return xp; }
    public Instant lastTestAt() { return lastTestAt; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

    public boolean canRetakeTest() {
        if (lastTestAt == null) {
            return true;
        }
        return Instant.now().isAfter(lastTestAt.plusSeconds(24 * 60 * 60));
    }
}

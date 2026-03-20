package com.faus535.englishtrainer.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private boolean testCompleted;
    private String levelListening;
    private String levelVocabulary;
    private String levelGrammar;
    private String levelPhrases;
    private String levelPronunciation;
    private int sessionCount;
    private int sessionsThisWeek;
    private LocalDate weekStart;
    private int xp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected UserProfile() {}

    public static UserProfile create() {
        var profile = new UserProfile();
        profile.testCompleted = false;
        profile.levelListening = "a1";
        profile.levelVocabulary = "a1";
        profile.levelGrammar = "a1";
        profile.levelPhrases = "a1";
        profile.levelPronunciation = "a1";
        profile.sessionCount = 0;
        profile.sessionsThisWeek = 0;
        profile.xp = 0;
        profile.createdAt = LocalDateTime.now();
        profile.updatedAt = LocalDateTime.now();
        return profile;
    }

    public UUID getId() { return id; }
    public boolean isTestCompleted() { return testCompleted; }
    public String getLevelListening() { return levelListening; }
    public String getLevelVocabulary() { return levelVocabulary; }
    public String getLevelGrammar() { return levelGrammar; }
    public String getLevelPhrases() { return levelPhrases; }
    public String getLevelPronunciation() { return levelPronunciation; }
    public int getSessionCount() { return sessionCount; }
    public int getSessionsThisWeek() { return sessionsThisWeek; }
    public LocalDate getWeekStart() { return weekStart; }
    public int getXp() { return xp; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void markTestCompleted() {
        this.testCompleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void setModuleLevel(String module, String level) {
        switch (module) {
            case "listening" -> this.levelListening = level;
            case "vocabulary" -> this.levelVocabulary = level;
            case "grammar" -> this.levelGrammar = level;
            case "phrases" -> this.levelPhrases = level;
            case "pronunciation" -> this.levelPronunciation = level;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void recordSession() {
        this.sessionCount++;
        this.sessionsThisWeek++;
        this.updatedAt = LocalDateTime.now();
    }

    public void addXp(int amount) {
        this.xp += amount;
        this.updatedAt = LocalDateTime.now();
    }
}

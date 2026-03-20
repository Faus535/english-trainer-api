package com.faus535.englishtrainer.gamification.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

public final class Achievement extends AggregateRoot<AchievementId> {

    private final AchievementId id;
    private final String name;
    private final String description;
    private final String icon;
    private final int xpReward;

    private Achievement(AchievementId id, String name, String description, String icon, int xpReward) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.xpReward = xpReward;
    }

    public static Achievement reconstitute(AchievementId id, String name, String description, String icon, int xpReward) {
        return new Achievement(id, name, description, icon, xpReward);
    }

    public AchievementId id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String icon() { return icon; }
    public int xpReward() { return xpReward; }
}

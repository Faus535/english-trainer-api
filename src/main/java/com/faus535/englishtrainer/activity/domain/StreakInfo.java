package com.faus535.englishtrainer.activity.domain;

import java.time.LocalDate;

public record StreakInfo(int currentStreak, int bestStreak, LocalDate lastActiveDate) {}

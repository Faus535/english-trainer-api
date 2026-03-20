package com.faus535.englishtrainer.curriculum.domain;

import java.util.List;

public record CurriculumDay(int dayNumber, String title, List<String> modules, String activityType, int estimatedMinutes) {}

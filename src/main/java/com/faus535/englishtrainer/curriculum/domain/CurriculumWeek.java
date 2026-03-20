package com.faus535.englishtrainer.curriculum.domain;

import java.util.List;

public record CurriculumWeek(int weekNumber, String theme, List<CurriculumDay> days) {}

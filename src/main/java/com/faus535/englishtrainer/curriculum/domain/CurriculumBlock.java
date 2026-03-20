package com.faus535.englishtrainer.curriculum.domain;

import java.util.List;

public record CurriculumBlock(int blockNumber, String name, String description, List<CurriculumWeek> weeks) {}

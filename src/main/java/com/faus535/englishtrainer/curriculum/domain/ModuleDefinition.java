package com.faus535.englishtrainer.curriculum.domain;

import java.util.List;
import java.util.Map;

public record ModuleDefinition(String name, String displayName, String description, int weight, Map<String, List<UnitDefinition>> levels) {}

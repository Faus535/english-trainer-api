package com.faus535.englishtrainer.analytics.domain;

import java.util.List;

public record WeaknessReport(
        List<String> weakModules,
        List<String> frequentGrammarErrors,
        List<String> suggestions
) {}

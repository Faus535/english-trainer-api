package com.faus535.englishtrainer.errorpattern.infrastructure.event;

import java.util.List;
import java.util.UUID;

public record GrammarFeedbackEvent(UUID userId, List<String> grammarCorrections) {}

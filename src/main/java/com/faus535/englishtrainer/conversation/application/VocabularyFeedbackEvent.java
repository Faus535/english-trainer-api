package com.faus535.englishtrainer.conversation.application;

import java.util.List;
import java.util.UUID;

public record VocabularyFeedbackEvent(UUID userId, String level, List<String> vocabularySuggestions) {}

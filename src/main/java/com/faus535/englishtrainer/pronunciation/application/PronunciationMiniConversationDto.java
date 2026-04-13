package com.faus535.englishtrainer.pronunciation.application;

import java.util.UUID;

public record PronunciationMiniConversationDto(UUID id, String prompt, String targetPhrase) {}

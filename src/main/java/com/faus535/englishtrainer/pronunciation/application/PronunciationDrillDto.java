package com.faus535.englishtrainer.pronunciation.application;

import java.util.UUID;

public record PronunciationDrillDto(UUID id, String phrase, String focus, String difficulty, String cefrLevel) {}

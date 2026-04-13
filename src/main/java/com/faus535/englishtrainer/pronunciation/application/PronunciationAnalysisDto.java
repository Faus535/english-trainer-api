package com.faus535.englishtrainer.pronunciation.application;

import java.util.List;

public record PronunciationAnalysisDto(
        String text,
        String ipa,
        String syllables,
        String stressPattern,
        List<String> tips,
        List<String> commonMistakes,
        List<String> minimalPairs,
        List<String> exampleSentences) {}

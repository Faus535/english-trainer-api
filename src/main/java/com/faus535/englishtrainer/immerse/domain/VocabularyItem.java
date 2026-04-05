package com.faus535.englishtrainer.immerse.domain;

public record VocabularyItem(
        String word,
        String definition,
        String exampleSentence,
        String cefrLevel
) {}

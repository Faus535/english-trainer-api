package com.faus535.englishtrainer.immerse.domain;

import com.faus535.englishtrainer.immerse.domain.error.ImmerseAiException;

import java.util.List;

public interface ImmerseAiPort {

    ImmerseProcessResult processContent(String rawText, String level) throws ImmerseAiException;

    ImmerseGenerateResult generateContent(ContentType contentType, String level, String topic) throws ImmerseAiException;

    record ImmerseProcessResult(
            String processedText,
            String detectedLevel,
            List<VocabularyItem> vocabulary,
            List<GeneratedExercise> exercises
    ) {}

    record ImmerseGenerateResult(
            String title,
            String processedText,
            String detectedLevel,
            List<VocabularyItem> vocabulary,
            List<GeneratedExercise> exercises
    ) {}

    record GeneratedExercise(
            String type,
            String question,
            String correctAnswer,
            List<String> options,
            String listenText,
            Integer blankPosition
    ) {}
}

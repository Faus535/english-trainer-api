package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseAiException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@UseCase
public class GenerateImmerseContentUseCase {

    private final ImmerseContentRepository contentRepository;
    private final ImmerseExerciseRepository exerciseRepository;
    private final ImmerseAiPort aiPort;

    public GenerateImmerseContentUseCase(ImmerseContentRepository contentRepository,
                                          ImmerseExerciseRepository exerciseRepository,
                                          ImmerseAiPort aiPort) {
        this.contentRepository = contentRepository;
        this.exerciseRepository = exerciseRepository;
        this.aiPort = aiPort;
    }

    @Transactional
    public ImmerseContent execute(UUID userId, ContentType contentType, String level, String topic)
            throws ImmerseAiException {

        ImmerseAiPort.ImmerseGenerateResult result = aiPort.generateContent(contentType, level, topic);

        ImmerseContent content = ImmerseContent.generate(
                userId, contentType, result.title(), result.rawText(),
                result.processedText(), result.detectedLevel(), result.vocabulary());

        ImmerseContent saved = contentRepository.save(content);

        AtomicInteger orderIndex = new AtomicInteger(0);
        List<ImmerseExercise> exercises = result.exercises().stream()
                .map(ge -> new ImmerseExercise(
                        ImmerseExerciseId.generate(), saved.id(),
                        ExerciseType.valueOf(ge.type()),
                        ge.question(), ge.correctAnswer(), ge.options(),
                        orderIndex.getAndIncrement()))
                .toList();

        if (!exercises.isEmpty()) {
            exerciseRepository.saveAll(exercises);
        }

        return saved;
    }
}

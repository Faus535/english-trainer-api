package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProcessImmerseContentAsyncService {

    private static final Logger log = LoggerFactory.getLogger(ProcessImmerseContentAsyncService.class);

    private final ImmerseContentRepository contentRepository;
    private final ImmerseExerciseRepository exerciseRepository;
    private final ImmerseAiPort aiPort;
    private final TransactionTemplate transactionTemplate;

    ProcessImmerseContentAsyncService(ImmerseContentRepository contentRepository,
                                      ImmerseExerciseRepository exerciseRepository,
                                      ImmerseAiPort aiPort,
                                      TransactionTemplate transactionTemplate) {
        this.contentRepository = contentRepository;
        this.exerciseRepository = exerciseRepository;
        this.aiPort = aiPort;
        this.transactionTemplate = transactionTemplate;
    }

    @Async("immerseAsyncExecutor")
    public void process(UUID contentId, ContentType contentType, String level, String topic) {
        try {
            ImmerseContent content = contentRepository.findById(new ImmerseContentId(contentId))
                    .orElse(null);

            if (content == null) {
                log.warn("Immerse content not found for async processing: {}", contentId);
                return;
            }

            ImmerseAiPort.ImmerseGenerateResult result = aiPort.generateContent(contentType, level, topic);

            ImmerseContent processed = content.markProcessed(
                    result.title(), null, result.processedText(),
                    result.detectedLevel(), result.vocabulary());

            AtomicInteger orderIndex = new AtomicInteger(0);
            List<ImmerseExercise> exercises = result.exercises().stream()
                    .map(ge -> new ImmerseExercise(
                            ImmerseExerciseId.generate(), processed.id(),
                            ExerciseType.valueOf(ge.type()),
                            ge.question(), ge.correctAnswer(), ge.options(),
                            orderIndex.getAndIncrement(), ge.listenText(), ge.blankPosition()))
                    .toList();

            transactionTemplate.executeWithoutResult(status -> {
                contentRepository.save(processed);
                if (!exercises.isEmpty()) {
                    exerciseRepository.saveAll(exercises);
                }
            });

            log.info("Immerse content processed successfully: {}", contentId);

        } catch (Throwable t) {
            log.error("Failed to process immerse content: {}", contentId, t);
            try {
                ImmerseContent content = contentRepository.findById(new ImmerseContentId(contentId))
                        .orElse(null);
                if (content != null) {
                    transactionTemplate.executeWithoutResult(status ->
                            contentRepository.save(content.markFailed()));
                }
            } catch (Exception e) {
                log.error("Failed to mark immerse content as FAILED: {}", contentId, e);
            }
        }
    }
}

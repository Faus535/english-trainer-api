package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationAiPort;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrill;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillId;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillRepository;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationDrillNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class SubmitDrillAttemptUseCase {

    private final PronunciationDrillRepository drillRepository;
    private final PronunciationAiPort pronunciationAiPort;
    private final ApplicationEventPublisher eventPublisher;

    public SubmitDrillAttemptUseCase(PronunciationDrillRepository drillRepository,
            PronunciationAiPort pronunciationAiPort,
            ApplicationEventPublisher eventPublisher) {
        this.drillRepository = drillRepository;
        this.pronunciationAiPort = pronunciationAiPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public DrillAttemptResultDto execute(UUID drillId, UUID userId, String recognizedText, double confidence)
            throws PronunciationDrillNotFoundException, PronunciationAiException {
        PronunciationDrill drill = drillRepository.findById(new PronunciationDrillId(drillId))
                .orElseThrow(() -> new PronunciationDrillNotFoundException(drillId));

        PronunciationAiPort.DrillFeedbackResult feedbackResult =
                pronunciationAiPort.drillFeedback(drill.phrase(), recognizedText, confidence);

        PronunciationDrill updated = drill.addAttempt(userId, feedbackResult.score(), recognizedText);
        int perfectStreak = updated.perfectStreakFor(userId);

        drillRepository.save(updated);
        updated.pullDomainEvents().forEach(eventPublisher::publishEvent);

        return new DrillAttemptResultDto(feedbackResult.score(), feedbackResult.feedback(), perfectStreak);
    }
}

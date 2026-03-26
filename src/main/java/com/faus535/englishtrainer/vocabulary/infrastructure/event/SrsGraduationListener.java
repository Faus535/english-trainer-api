package com.faus535.englishtrainer.vocabulary.infrastructure.event;

import com.faus535.englishtrainer.spacedrepetition.domain.event.ReviewCompletedEvent;
import com.faus535.englishtrainer.vocabulary.domain.VocabMastery;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class SrsGraduationListener {

    private static final Logger log = LoggerFactory.getLogger(SrsGraduationListener.class);
    private static final String VOCABULARY_WORD_TYPE = "vocabulary-word";
    private static final String VOCAB_PREFIX = "vocab-";

    private final VocabMasteryRepository vocabMasteryRepository;
    private final ApplicationEventPublisher eventPublisher;

    SrsGraduationListener(VocabMasteryRepository vocabMasteryRepository,
                           ApplicationEventPublisher eventPublisher) {
        this.vocabMasteryRepository = vocabMasteryRepository;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    void handleReviewCompleted(ReviewCompletedEvent event) {
        if (!event.graduated() || !VOCABULARY_WORD_TYPE.equals(event.itemType())) {
            return;
        }

        String unitReference = event.unitReference();
        if (unitReference == null || !unitReference.startsWith(VOCAB_PREFIX)) {
            log.warn("Graduated vocabulary item has invalid unitReference: {}", unitReference);
            return;
        }

        String word = unitReference.substring(VOCAB_PREFIX.length());

        try {
            vocabMasteryRepository.findByUserIdAndWord(event.userId(), word)
                    .ifPresentOrElse(
                            mastery -> {
                                VocabMastery mastered = mastery.graduate();
                                vocabMasteryRepository.save(mastered);
                                mastered.pullDomainEvents().forEach(eventPublisher::publishEvent);
                                log.info("Word '{}' graduated to MASTERED for user {}", word, event.userId());
                            },
                            () -> log.warn("No VocabMastery found for word '{}' and user {}", word, event.userId())
                    );
        } catch (Exception e) {
            log.error("Error graduating word '{}' for user {}: {}", word, event.userId(), e.getMessage(), e);
        }
    }
}

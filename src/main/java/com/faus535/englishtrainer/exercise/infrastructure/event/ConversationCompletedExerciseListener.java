package com.faus535.englishtrainer.exercise.infrastructure.event;

import com.faus535.englishtrainer.conversation.domain.ConversationRepository;
import com.faus535.englishtrainer.conversation.domain.TutorFeedback;
import com.faus535.englishtrainer.conversation.domain.event.ConversationCompletedEvent;
import com.faus535.englishtrainer.exercise.application.GenerateExercisesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class ConversationCompletedExerciseListener {

    private static final Logger log = LoggerFactory.getLogger(ConversationCompletedExerciseListener.class);

    private final ConversationRepository conversationRepository;
    private final GenerateExercisesUseCase generateExercisesUseCase;

    ConversationCompletedExerciseListener(ConversationRepository conversationRepository,
                                           GenerateExercisesUseCase generateExercisesUseCase) {
        this.conversationRepository = conversationRepository;
        this.generateExercisesUseCase = generateExercisesUseCase;
    }

    @EventListener
    void handleConversationCompleted(ConversationCompletedEvent event) {
        try {
            conversationRepository.findById(event.conversationId()).ifPresent(conversation -> {
                List<String> errors = new ArrayList<>();
                conversation.turns().stream()
                        .filter(t -> t.feedback() != null)
                        .forEach(t -> {
                            TutorFeedback fb = t.feedback();
                            errors.addAll(fb.grammarCorrections());
                            errors.addAll(fb.vocabularySuggestions());
                        });

                if (!errors.isEmpty()) {
                    generateExercisesUseCase.execute(
                            event.conversationId().value(), event.userId(),
                            conversation.level().value(), errors);
                    log.info("Generated exercises for conversation {} with {} errors",
                            event.conversationId().value(), errors.size());
                }
            });
        } catch (Exception e) {
            log.error("Error generating exercises for conversation {}: {}",
                    event.conversationId().value(), e.getMessage(), e);
        }
    }
}

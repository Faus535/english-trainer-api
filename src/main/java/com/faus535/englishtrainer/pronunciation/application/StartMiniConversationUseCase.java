package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationAiPort;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversation;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversationRepository;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class StartMiniConversationUseCase {

    private final PronunciationMiniConversationRepository conversationRepository;
    private final PronunciationAiPort pronunciationAiPort;

    public StartMiniConversationUseCase(PronunciationMiniConversationRepository conversationRepository,
            PronunciationAiPort pronunciationAiPort) {
        this.conversationRepository = conversationRepository;
        this.pronunciationAiPort = pronunciationAiPort;
    }

    @Transactional
    public PronunciationMiniConversationDto execute(UUID userId, String focus, String level)
            throws PronunciationAiException {
        PronunciationAiPort.MiniConversationResult aiResult =
                pronunciationAiPort.startMiniConversation(focus, level);

        PronunciationMiniConversation conversation = PronunciationMiniConversation.start(
                userId, focus, level, aiResult.prompt(), aiResult.targetPhrase());

        PronunciationMiniConversation saved = conversationRepository.save(conversation);

        return new PronunciationMiniConversationDto(
                saved.id().value(), saved.currentPrompt(), saved.currentTargetPhrase());
    }
}

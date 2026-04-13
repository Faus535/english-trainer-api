package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationAiPort;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversation;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversationId;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversationRepository;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationMiniConversationNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class EvaluateMiniConversationTurnUseCase {

    private final PronunciationMiniConversationRepository conversationRepository;
    private final PronunciationAiPort pronunciationAiPort;
    private final ApplicationEventPublisher eventPublisher;

    public EvaluateMiniConversationTurnUseCase(PronunciationMiniConversationRepository conversationRepository,
            PronunciationAiPort pronunciationAiPort,
            ApplicationEventPublisher eventPublisher) {
        this.conversationRepository = conversationRepository;
        this.pronunciationAiPort = pronunciationAiPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public MiniConversationTurnResultDto execute(UUID conversationId, String recognizedText,
            List<WordConfidenceDto> wordConfidences)
            throws PronunciationMiniConversationNotFoundException, PronunciationAiException {
        PronunciationMiniConversation conversation = conversationRepository
                .findById(new PronunciationMiniConversationId(conversationId))
                .orElseThrow(() -> new PronunciationMiniConversationNotFoundException(conversationId));

        List<PronunciationAiPort.WordConfidence> portConfidences = wordConfidences.stream()
                .map(wc -> new PronunciationAiPort.WordConfidence(wc.word(), wc.confidence()))
                .toList();

        PronunciationAiPort.MiniConversationTurnResult aiResult =
                pronunciationAiPort.evaluateMiniConversationTurn(
                        conversation.currentTargetPhrase(), recognizedText,
                        portConfidences, conversation.focus(), conversation.level());

        PronunciationMiniConversation updated = conversation.evaluateTurn(
                recognizedText, aiResult.score(), aiResult.nextPrompt(), aiResult.nextTargetPhrase());

        PronunciationMiniConversation saved = conversationRepository.save(updated);
        saved.pullDomainEvents().forEach(eventPublisher::publishEvent);

        List<WordFeedbackDto> wordFeedback = aiResult.wordFeedback().stream()
                .map(wf -> new WordFeedbackDto(wf.word(), wf.recognized(), wf.tip(), wf.score()))
                .toList();

        return new MiniConversationTurnResultDto(
                aiResult.score(), wordFeedback,
                aiResult.nextPrompt(), aiResult.nextTargetPhrase(),
                saved.isComplete());
    }
}

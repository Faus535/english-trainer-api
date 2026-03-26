package com.faus535.englishtrainer.minigame.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.MasterySource;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabMastery;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryRepository;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@UseCase
public class SaveGameResultsUseCase {

    private final SaveMiniGameScoreUseCase saveMiniGameScoreUseCase;
    private final VocabMasteryRepository vocabMasteryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SaveGameResultsUseCase(SaveMiniGameScoreUseCase saveMiniGameScoreUseCase,
                                   VocabMasteryRepository vocabMasteryRepository,
                                   ApplicationEventPublisher eventPublisher) {
        this.saveMiniGameScoreUseCase = saveMiniGameScoreUseCase;
        this.vocabMasteryRepository = vocabMasteryRepository;
        this.eventPublisher = eventPublisher;
    }

    public record AnsweredItem(UUID vocabEntryId, String word, String level, boolean correct) {}

    public record SaveGameResult(int score, int xpEarned, List<String> wordsLearned,
                                  int wordsAddedToReview, int totalWordsEncountered) {}

    @Transactional
    public SaveGameResult execute(UserProfileId userId, String gameType, int score,
                                   List<AnsweredItem> answeredItems) {
        SaveMiniGameScoreUseCase.SaveScoreResult scoreResult =
                saveMiniGameScoreUseCase.execute(userId, gameType, score);

        List<String> wordsLearned = new ArrayList<>();
        int wordsAddedToReview = 0;
        List<Object> allDomainEvents = new ArrayList<>();

        for (AnsweredItem item : answeredItems) {
            if (item.vocabEntryId() == null) {
                continue;
            }

            VocabEntryId vocabEntryId = new VocabEntryId(item.vocabEntryId());
            Optional<VocabMastery> existingMastery =
                    vocabMasteryRepository.findByUserIdAndVocabEntryId(userId, vocabEntryId);

            VocabMastery mastery;
            if (existingMastery.isPresent()) {
                mastery = existingMastery.get();
            } else {
                mastery = VocabMastery.create(userId, vocabEntryId, item.word(),
                        new MasterySource("minigame", gameType));
                wordsAddedToReview++;
            }

            VocabMasteryStatus statusBefore = mastery.status();

            if (item.correct()) {
                mastery = mastery.recordCorrectAnswer();
            } else {
                mastery = mastery.recordIncorrectAnswer();
            }

            List<Object> events = mastery.pullDomainEvents();
            allDomainEvents.addAll(events);

            if (mastery.status() == VocabMasteryStatus.LEARNED
                    && statusBefore != VocabMasteryStatus.LEARNED
                    && statusBefore != VocabMasteryStatus.MASTERED) {
                wordsLearned.add(item.word());
            }

            vocabMasteryRepository.save(mastery);
        }

        for (Object event : allDomainEvents) {
            eventPublisher.publishEvent(event);
        }

        return new SaveGameResult(
                scoreResult.score(),
                scoreResult.xpEarned(),
                wordsLearned,
                wordsAddedToReview,
                answeredItems.size()
        );
    }
}

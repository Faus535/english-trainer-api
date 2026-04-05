package com.faus535.englishtrainer.home.application;

import com.faus535.englishtrainer.review.domain.ReviewItemRepository;
import com.faus535.englishtrainer.review.domain.ReviewStats;
import com.faus535.englishtrainer.review.application.GetReviewStatsUseCase;
import com.faus535.englishtrainer.talk.domain.TalkConversation;
import com.faus535.englishtrainer.talk.domain.TalkConversationRepository;
import com.faus535.englishtrainer.talk.domain.TalkStatus;
import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetHomeUseCase {

    private final TalkConversationRepository talkRepository;
    private final ImmerseContentRepository immerseRepository;
    private final GetReviewStatsUseCase getReviewStatsUseCase;

    public GetHomeUseCase(TalkConversationRepository talkRepository,
                           ImmerseContentRepository immerseRepository,
                           GetReviewStatsUseCase getReviewStatsUseCase) {
        this.talkRepository = talkRepository;
        this.immerseRepository = immerseRepository;
        this.getReviewStatsUseCase = getReviewStatsUseCase;
    }

    public HomeData execute(UUID userId) {
        // Talk summary
        List<TalkConversation> talkConversations = talkRepository.findByUserId(userId);
        boolean hasActive = talkConversations.stream()
                .anyMatch(c -> c.status() == TalkStatus.ACTIVE);
        List<TalkConversation> completed = talkConversations.stream()
                .filter(c -> c.status() == TalkStatus.COMPLETED)
                .toList();
        Integer lastScore = completed.stream()
                .reduce((a, b) -> b)
                .map(c -> c.evaluation() != null ? c.evaluation().overallScore() : null)
                .orElse(null);

        HomeData.TalkSummary talkSummary = new HomeData.TalkSummary(
                hasActive, completed.size(), lastScore);

        // Immerse summary
        var immersePage = immerseRepository.findByUserId(userId, 0, 5);
        String lastTitle = immersePage.isEmpty() ? null : immersePage.getFirst().title();

        HomeData.ImmerseSummary immerseSummary = new HomeData.ImmerseSummary(
                immersePage.size(), lastTitle);

        // Review summary
        ReviewStats reviewStats = getReviewStatsUseCase.execute(userId);

        HomeData.ReviewSummary reviewSummary = new HomeData.ReviewSummary(
                reviewStats.dueToday(), reviewStats.streak());

        // Suggest action
        String suggestedAction;
        if (reviewStats.dueToday() > 0) {
            suggestedAction = "REVIEW";
        } else if (!hasActive) {
            suggestedAction = "TALK";
        } else {
            suggestedAction = "IMMERSE";
        }

        return new HomeData(suggestedAction, talkSummary, immerseSummary, reviewSummary);
    }
}

package com.faus535.englishtrainer.home.application;

import com.faus535.englishtrainer.immerse.domain.ImmerseContentMother;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewResultRepository;
import com.faus535.englishtrainer.review.application.GetReviewStatsUseCase;
import com.faus535.englishtrainer.review.domain.ReviewItemMother;
import com.faus535.englishtrainer.talk.domain.TalkConversationMother;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetHomeUseCaseTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private InMemoryTalkConversationRepository talkRepository;
    private InMemoryImmerseContentRepository immerseRepository;
    private InMemoryReviewItemRepository reviewItemRepository;
    private InMemoryReviewResultRepository reviewResultRepository;
    private GetHomeUseCase useCase;

    @BeforeEach
    void setUp() {
        talkRepository = new InMemoryTalkConversationRepository();
        immerseRepository = new InMemoryImmerseContentRepository();
        reviewItemRepository = new InMemoryReviewItemRepository();
        reviewResultRepository = new InMemoryReviewResultRepository();
        var getReviewStatsUseCase = new GetReviewStatsUseCase(reviewItemRepository, reviewResultRepository);
        useCase = new GetHomeUseCase(talkRepository, immerseRepository, getReviewStatsUseCase);
    }

    @Test
    void shouldReturnSuggestedActionReviewWhenDueItemsExist() {
        reviewItemRepository.save(ReviewItemMother.dueToday());

        HomeData result = useCase.execute(USER_ID);

        assertEquals("REVIEW", result.suggestedAction());
        assertEquals(1, result.review().dueToday());
    }

    @Test
    void shouldReturnSuggestedActionTalkWhenNoActiveConversation() {
        // No due review items, no active talk conversation
        HomeData result = useCase.execute(USER_ID);

        assertEquals("TALK", result.suggestedAction());
        assertFalse(result.talk().hasActiveConversation());
    }

    @Test
    void shouldReturnSuggestedActionImmerseAsDefault() {
        // Active talk conversation, no due review items -> IMMERSE
        talkRepository.save(TalkConversationMother.active());

        HomeData result = useCase.execute(USER_ID);

        assertEquals("IMMERSE", result.suggestedAction());
        assertTrue(result.talk().hasActiveConversation());
    }
}

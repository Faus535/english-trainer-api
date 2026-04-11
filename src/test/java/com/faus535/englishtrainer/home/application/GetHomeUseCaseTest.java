package com.faus535.englishtrainer.home.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.infrastructure.InMemoryActivityDateRepository;
import com.faus535.englishtrainer.article.domain.ArticleReadingMother;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.gamification.domain.AchievementId;
import com.faus535.englishtrainer.gamification.domain.AchievementMother;
import com.faus535.englishtrainer.gamification.domain.UserAchievement;
import com.faus535.englishtrainer.gamification.domain.UserAchievementId;
import com.faus535.englishtrainer.gamification.infrastructure.InMemoryAchievementRepository;
import com.faus535.englishtrainer.gamification.infrastructure.InMemoryUserAchievementRepository;
import com.faus535.englishtrainer.immerse.domain.ImmerseExerciseId;
import com.faus535.englishtrainer.immerse.domain.ImmerseSubmission;
import com.faus535.englishtrainer.immerse.domain.ImmerseSubmissionId;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseSubmissionRepository;
import com.faus535.englishtrainer.review.domain.ReviewItemMother;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.vo.EnglishLevel;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetHomeUseCaseTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UserProfileId PROFILE_ID = new UserProfileId(USER_ID);

    private InMemoryActivityDateRepository activityDateRepo;
    private InMemoryReviewItemRepository reviewItemRepo;
    private InMemoryArticleReadingRepository articleReadingRepo;
    private InMemoryImmerseSubmissionRepository immerseSubmissionRepo;
    private InMemoryUserProfileRepository userProfileRepo;
    private InMemoryUserAchievementRepository userAchievementRepo;
    private InMemoryAchievementRepository achievementRepo;
    private GetHomeUseCase useCase;

    @BeforeEach
    void setUp() {
        activityDateRepo = new InMemoryActivityDateRepository();
        reviewItemRepo = new InMemoryReviewItemRepository();
        articleReadingRepo = new InMemoryArticleReadingRepository();
        immerseSubmissionRepo = new InMemoryImmerseSubmissionRepository();
        userProfileRepo = new InMemoryUserProfileRepository();
        userAchievementRepo = new InMemoryUserAchievementRepository();
        achievementRepo = new InMemoryAchievementRepository();
        useCase = new GetHomeUseCase(activityDateRepo, reviewItemRepo, articleReadingRepo,
                immerseSubmissionRepo, userProfileRepo, userAchievementRepo, achievementRepo);

        UserProfile profile = UserProfile.reconstitute(PROFILE_ID, null, 0, null,
                Instant.now(), Instant.now());
        userProfileRepo.save(profile);
    }

    @Test
    void execute_suggestsReview_whenDueCountIsThreeOrMore() {
        reviewItemRepo.save(ReviewItemMother.dueToday());
        reviewItemRepo.save(ReviewItemMother.dueToday());
        reviewItemRepo.save(ReviewItemMother.dueToday());

        HomeData result = useCase.execute(USER_ID);

        assertEquals(HomeData.SuggestedModule.REVIEW, result.suggestedModule());
        assertEquals(3, result.dueReviewCount());
    }

    @Test
    void execute_suggestsArticle_whenNoArticleToday() {
        HomeData result = useCase.execute(USER_ID);

        assertEquals(HomeData.SuggestedModule.ARTICLE, result.suggestedModule());
    }

    @Test
    void execute_suggestsImmerse_whenNoImmerseToday() {
        articleReadingRepo.save(ArticleReadingMother.inProgress(USER_ID));

        HomeData result = useCase.execute(USER_ID);

        assertEquals(HomeData.SuggestedModule.IMMERSE, result.suggestedModule());
    }

    @Test
    void execute_suggestsTalk_whenAllOtherModulesDoneToday() {
        articleReadingRepo.save(ArticleReadingMother.inProgress(USER_ID));
        immerseSubmissionRepo.save(submissionForUser(USER_ID, Instant.now()));

        HomeData result = useCase.execute(USER_ID);

        assertEquals(HomeData.SuggestedModule.TALK, result.suggestedModule());
    }

    @Test
    void execute_computesStreak() {
        activityDateRepo.save(ActivityDate.create(PROFILE_ID, LocalDate.now(ZoneOffset.UTC)));
        activityDateRepo.save(ActivityDate.create(PROFILE_ID, LocalDate.now(ZoneOffset.UTC).minusDays(1)));

        HomeData result = useCase.execute(USER_ID);

        assertEquals(2, result.streakDays());
    }

    @Test
    void execute_computesWeeklyActivity() {
        LocalDate monday = LocalDate.now(ZoneOffset.UTC).with(java.time.DayOfWeek.MONDAY);
        activityDateRepo.save(ActivityDate.create(PROFILE_ID, monday));

        HomeData result = useCase.execute(USER_ID);

        assertTrue(result.weeklyActivity()[0]);
        assertEquals(7, result.weeklyActivity().length);
    }

    @Test
    void execute_includesEnglishLevel() {
        UserProfile profile = UserProfile.reconstitute(PROFILE_ID, null, 0, EnglishLevel.B1,
                Instant.now(), Instant.now());
        userProfileRepo.save(profile);

        HomeData result = useCase.execute(USER_ID);

        assertEquals("B1", result.englishLevel());
    }

    @Test
    void execute_includesRecentAchievements() {
        Achievement achievement = AchievementMother.create();
        achievementRepo.save(achievement);
        UserAchievement ua = UserAchievement.reconstitute(
                UserAchievementId.generate(), PROFILE_ID, achievement.id(), Instant.now());
        userAchievementRepo.save(ua);

        HomeData result = useCase.execute(USER_ID);

        assertEquals(1, result.recentAchievements().size());
        assertEquals(achievement.name(), result.recentAchievements().getFirst().title());
        assertEquals(achievement.icon(), result.recentAchievements().getFirst().icon());
        assertEquals(achievement.xpReward(), result.recentAchievements().getFirst().xpReward());
    }

    private ImmerseSubmission submissionForUser(UUID userId, Instant submittedAt) {
        return ImmerseSubmission.reconstitute(
                ImmerseSubmissionId.generate(), ImmerseExerciseId.generate(),
                userId, "answer", true, "Correct!", submittedAt);
    }
}

package com.faus535.englishtrainer.home.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.article.domain.ArticleReadingRepository;
import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.gamification.domain.AchievementRepository;
import com.faus535.englishtrainer.gamification.domain.UserAchievement;
import com.faus535.englishtrainer.gamification.domain.UserAchievementRepository;
import com.faus535.englishtrainer.immerse.domain.ImmerseSubmissionRepository;
import com.faus535.englishtrainer.review.domain.ReviewItemRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
public class GetHomeUseCase {

    private final ActivityDateRepository activityDateRepo;
    private final ReviewItemRepository reviewItemRepo;
    private final ArticleReadingRepository articleReadingRepo;
    private final ImmerseSubmissionRepository immerseSubmissionRepo;
    private final UserProfileRepository userProfileRepo;
    private final UserAchievementRepository userAchievementRepo;
    private final AchievementRepository achievementRepo;

    GetHomeUseCase(ActivityDateRepository activityDateRepo,
                   ReviewItemRepository reviewItemRepo,
                   ArticleReadingRepository articleReadingRepo,
                   ImmerseSubmissionRepository immerseSubmissionRepo,
                   UserProfileRepository userProfileRepo,
                   UserAchievementRepository userAchievementRepo,
                   AchievementRepository achievementRepo) {
        this.activityDateRepo = activityDateRepo;
        this.reviewItemRepo = reviewItemRepo;
        this.articleReadingRepo = articleReadingRepo;
        this.immerseSubmissionRepo = immerseSubmissionRepo;
        this.userProfileRepo = userProfileRepo;
        this.userAchievementRepo = userAchievementRepo;
        this.achievementRepo = achievementRepo;
    }

    public HomeData execute(UUID userId) {
        UserProfileId profileId = new UserProfileId(userId);

        List<ActivityDate> activities = activityDateRepo.findAllByUser(profileId);
        int streakDays = computeStreak(activities);
        boolean[] weeklyActivity = computeWeeklyActivity(activities);

        long dueReviewCount = reviewItemRepo.countDueByUserId(userId, Instant.now());

        Instant todayStart = LocalDate.now(ZoneOffset.UTC).atStartOfDay(ZoneOffset.UTC).toInstant();
        boolean hasArticleToday = articleReadingRepo.existsByUserIdAndCreatedAtAfter(userId, todayStart);
        long immerseToday = immerseSubmissionRepo.countByUserIdAndCreatedAtAfter(userId, todayStart);

        HomeData.SuggestedModule suggestedModule;
        if (dueReviewCount >= 3) {
            suggestedModule = HomeData.SuggestedModule.REVIEW;
        } else if (!hasArticleToday) {
            suggestedModule = HomeData.SuggestedModule.ARTICLE;
        } else if (immerseToday == 0) {
            suggestedModule = HomeData.SuggestedModule.IMMERSE;
        } else {
            suggestedModule = HomeData.SuggestedModule.TALK;
        }

        UserProfile profile = userProfileRepo.findById(profileId).orElseThrow();
        String englishLevel = profile.englishLevel() != null ? profile.englishLevel().name() : null;

        Instant weekStart = LocalDate.now(ZoneOffset.UTC).with(DayOfWeek.MONDAY)
                .atStartOfDay(ZoneOffset.UTC).toInstant();
        long recentXpThisWeek = userAchievementRepo.findByUserAndUnlockedAtAfter(profileId, weekStart)
                .stream()
                .mapToLong(ua -> achievementRepo.findById(ua.achievementId())
                        .map(Achievement::xpReward).orElse(0))
                .sum();

        List<HomeData.RecentAchievement> recentAchievements = userAchievementRepo
                .findTop3ByUserOrderByUnlockedAtDesc(profileId)
                .stream()
                .map(ua -> achievementRepo.findById(ua.achievementId())
                        .map(a -> new HomeData.RecentAchievement(a.name(), a.icon(), a.xpReward()))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();

        return new HomeData(dueReviewCount, streakDays, weeklyActivity, suggestedModule,
                recentXpThisWeek, recentAchievements, englishLevel);
    }

    private int computeStreak(List<ActivityDate> activities) {
        if (activities.isEmpty()) return 0;
        Set<LocalDate> dates = activities.stream()
                .map(ActivityDate::activityDate)
                .collect(Collectors.toSet());
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        int streak = 0;
        LocalDate cursor = today;
        while (dates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private boolean[] computeWeeklyActivity(List<ActivityDate> activities) {
        Set<LocalDate> dates = activities.stream()
                .map(ActivityDate::activityDate)
                .collect(Collectors.toSet());
        LocalDate monday = LocalDate.now(ZoneOffset.UTC).with(DayOfWeek.MONDAY);
        boolean[] weekly = new boolean[7];
        for (int i = 0; i < 7; i++) {
            weekly[i] = dates.contains(monday.plusDays(i));
        }
        return weekly;
    }
}

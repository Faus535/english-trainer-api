package com.faus535.englishtrainer.analytics.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.activity.domain.StreakCalculator;
import com.faus535.englishtrainer.activity.domain.StreakInfo;
import com.faus535.englishtrainer.analytics.domain.AnalyticsSummary;
import com.faus535.englishtrainer.conversation.domain.ConversationRepository;
import com.faus535.englishtrainer.reading.domain.ReadingSubmissionRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.writing.domain.WritingSubmissionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UseCase
public class GetAnalyticsSummaryUseCase {

    private final UserProfileRepository profileRepository;
    private final ConversationRepository conversationRepository;
    private final ReadingSubmissionRepository readingRepository;
    private final WritingSubmissionRepository writingRepository;
    private final ActivityDateRepository activityRepository;

    public GetAnalyticsSummaryUseCase(UserProfileRepository profileRepository,
                                       ConversationRepository conversationRepository,
                                       ReadingSubmissionRepository readingRepository,
                                       WritingSubmissionRepository writingRepository,
                                       ActivityDateRepository activityRepository) {
        this.profileRepository = profileRepository;
        this.conversationRepository = conversationRepository;
        this.readingRepository = readingRepository;
        this.writingRepository = writingRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public AnalyticsSummary execute(UUID userId) {
        UserProfileId profileId = new UserProfileId(userId);
        UserProfile profile = profileRepository.findById(profileId).orElse(null);

        int totalSessions = profile != null ? profile.sessionCount() : 0;
        int totalXp = profile != null ? profile.xp() : 0;

        int totalConversations = conversationRepository.findByUserId(userId).size();
        int totalReading = readingRepository.findByUserId(userId).size();
        int totalWriting = writingRepository.findByUserId(userId).size();

        List<LocalDate> activityDates = activityRepository.findAllByUser(profileId).stream()
                .map(ActivityDate::activityDate)
                .sorted(java.util.Comparator.reverseOrder())
                .toList();
        StreakInfo streak = StreakCalculator.calculate(activityDates);

        Map<String, String> currentLevels = new HashMap<>();
        if (profile != null) {
            currentLevels.put("listening", profile.levelListening().value());
            currentLevels.put("vocabulary", profile.levelVocabulary().value());
            currentLevels.put("grammar", profile.levelGrammar().value());
            currentLevels.put("phrases", profile.levelPhrases().value());
            currentLevels.put("pronunciation", profile.levelPronunciation().value());
        }

        return new AnalyticsSummary(
                totalSessions, totalConversations, totalReading, totalWriting,
                streak.currentStreak(), streak.bestStreak(), totalXp, currentLevels);
    }
}

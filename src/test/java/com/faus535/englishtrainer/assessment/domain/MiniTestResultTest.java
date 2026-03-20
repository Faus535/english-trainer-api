package com.faus535.englishtrainer.assessment.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MiniTestResultTest {

    @Test
    void shouldCreateMiniTestResult() {
        UserProfileId userId = UserProfileId.generate();

        MiniTestResult result = MiniTestResult.create(userId, "vocabulary", "b1", 70, 20, 14);

        assertNotNull(result.id());
        assertEquals(userId, result.userId());
        assertEquals("vocabulary", result.moduleName());
        assertEquals("b1", result.level());
        assertEquals(70, result.score());
        assertEquals(20, result.totalQuestions());
        assertEquals(14, result.correctAnswers());
        assertNotNull(result.completedAt());
    }

    @Test
    void shouldRecommendDemoteWhenScoreBelow60() {
        MiniTestResult result = MiniTestResult.create(
                UserProfileId.generate(), "grammar", "b1", 45, 20, 9
        );

        assertEquals("demote", result.recommendation());
    }

    @Test
    void shouldRecommendPromoteWhenScoreAbove85() {
        MiniTestResult result = MiniTestResult.create(
                UserProfileId.generate(), "grammar", "b1", 90, 20, 18
        );

        assertEquals("promote", result.recommendation());
    }

    @Test
    void shouldRecommendMaintainWhenScoreBetween() {
        MiniTestResult result = MiniTestResult.create(
                UserProfileId.generate(), "grammar", "b1", 70, 20, 14
        );

        assertEquals("maintain", result.recommendation());
    }
}

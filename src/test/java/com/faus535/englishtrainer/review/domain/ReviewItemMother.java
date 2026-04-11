package com.faus535.englishtrainer.review.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class ReviewItemMother {

    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static ReviewItem dueToday() {
        return ReviewItem.reconstitute(
                ReviewItemId.generate(), DEFAULT_USER_ID, ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "I want to order a coffee",
                "I'd like to order a coffee (use polite form)",
                LocalDate.now().minusDays(1), 1, 2.5, 0, Instant.now().minusSeconds(86400),
                null, null, null, null);
    }

    public static ReviewItem notDue() {
        return ReviewItem.reconstitute(
                ReviewItemId.generate(), DEFAULT_USER_ID, ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "She go to school",
                "She goes to school (third person -s)",
                LocalDate.now().plusDays(3), 6, 2.5, 1, Instant.now().minusSeconds(86400 * 3),
                null, null, null, null);
    }

    public static ReviewItem fromTalkError() {
        return ReviewItem.create(DEFAULT_USER_ID, ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "I have 20 years old",
                "I am 20 years old (use 'to be' for age)");
    }

    public static ReviewItem fromImmerseVocab() {
        return ReviewItem.create(DEFAULT_USER_ID, ReviewSourceType.IMMERSE_VOCAB,
                UUID.randomUUID(), "What does 'drought' mean?",
                "A long period without rain");
    }

    public static ReviewItem withEaseFactor(double easeFactor) {
        return ReviewItem.reconstitute(
                ReviewItemId.generate(), DEFAULT_USER_ID, ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "Test front", "Test back",
                LocalDate.now().minusDays(1), 6, easeFactor, 2,
                Instant.now().minusSeconds(86400 * 7),
                null, null, null, null);
    }

    public static ReviewItem withRepetitions(int repetitions) {
        return ReviewItem.reconstitute(
                ReviewItemId.generate(), DEFAULT_USER_ID, ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "Test front", "Test back",
                LocalDate.now().minusDays(1), 6, 2.5, repetitions,
                Instant.now().minusSeconds(86400 * 7),
                null, null, null, null);
    }

    public static ReviewItem withUserId(UUID userId) {
        return ReviewItem.create(userId, ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "Test front", "Test back");
    }

    public static ReviewItem withContext(String contextSentence, String contextTranslation,
                                          String targetWord, String targetTranslation) {
        return ReviewItem.create(DEFAULT_USER_ID, ReviewSourceType.ARTICLE,
                UUID.randomUUID(), targetWord, targetTranslation,
                contextSentence, contextTranslation, targetWord, targetTranslation);
    }

    public static ReviewItem withIntervalDays(UUID userId, int intervalDays) {
        return ReviewItem.reconstitute(
                ReviewItemId.generate(), userId, ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "Test front", "Test back",
                LocalDate.now().plusDays(intervalDays), intervalDays, 2.5, 2,
                Instant.now().minusSeconds(86400),
                null, null, null, null);
    }
}

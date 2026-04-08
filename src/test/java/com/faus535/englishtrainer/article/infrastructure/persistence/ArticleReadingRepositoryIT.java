package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.IntegrationTestBase;
import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleReadingRepositoryIT extends IntegrationTestBase {

    @Autowired
    private ArticleReadingRepository articleReadingRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private UUID userId;

    @BeforeEach
    void setUpUser() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);
        userId = userProfile.id().value();
    }

    @Test
    void save_and_find_by_id_round_trip() {
        ArticleReading article = ArticleReading.create(userId, new ArticleTopic("Climate change"), ArticleLevel.B2);
        articleReadingRepository.save(article);

        Optional<ArticleReading> found = articleReadingRepository.findById(article.id());

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(article.id());
        assertThat(found.get().userId()).isEqualTo(userId);
        assertThat(found.get().topic().value()).isEqualTo("Climate change");
        assertThat(found.get().level()).isEqualTo(ArticleLevel.B2);
        assertThat(found.get().status()).isEqualTo(ArticleStatus.PENDING);
    }

    @Test
    void find_by_id_returns_empty_when_not_found() {
        Optional<ArticleReading> found = articleReadingRepository.findById(ArticleReadingId.generate());

        assertThat(found).isEmpty();
    }

    @Test
    void find_by_user_ordered_by_created_at_desc() {
        Instant older = Instant.parse("2026-04-01T10:00:00Z");
        Instant newer = Instant.parse("2026-04-08T10:00:00Z");

        ArticleReading olderArticle = ArticleReading.reconstitute(
                ArticleReadingId.generate(), userId, new ArticleTopic("Old"),
                ArticleLevel.B1, "Old Article", ArticleStatus.IN_PROGRESS, List.of(), 0, older);
        ArticleReading newerArticle = ArticleReading.reconstitute(
                ArticleReadingId.generate(), userId, new ArticleTopic("New"),
                ArticleLevel.B2, "New Article", ArticleStatus.IN_PROGRESS, List.of(), 0, newer);

        articleReadingRepository.save(olderArticle);
        articleReadingRepository.save(newerArticle);

        List<ArticleReading> result = articleReadingRepository.findByUserIdOrderByCreatedAtDesc(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("New Article");
        assertThat(result.get(1).title()).isEqualTo("Old Article");
    }

    @Test
    void find_by_user_returns_empty_for_different_user() {
        ArticleReading article = ArticleReading.create(userId, new ArticleTopic("Tech"), ArticleLevel.B1);
        articleReadingRepository.save(article);

        List<ArticleReading> result = articleReadingRepository.findByUserIdOrderByCreatedAtDesc(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void delete_by_id_removes_article() {
        ArticleReading article = ArticleReading.create(userId, new ArticleTopic("Tech"), ArticleLevel.B1);
        articleReadingRepository.save(article);

        articleReadingRepository.deleteById(article.id());

        assertThat(articleReadingRepository.findById(article.id())).isEmpty();
    }

    @Test
    void update_preserves_status_and_xp() throws Exception {
        ArticleReading article = ArticleReading.reconstitute(
                ArticleReadingId.generate(), userId, new ArticleTopic("Tech"),
                ArticleLevel.B2, "Test Article", ArticleStatus.IN_PROGRESS,
                List.of(), 0, Instant.now());
        articleReadingRepository.save(article);

        ArticleReading completed = article.complete(41);
        articleReadingRepository.save(completed);

        Optional<ArticleReading> found = articleReadingRepository.findById(article.id());

        assertThat(found).isPresent();
        assertThat(found.get().status()).isEqualTo(ArticleStatus.COMPLETED);
        assertThat(found.get().xpEarned()).isEqualTo(41);
    }

    @Test
    void save_with_paragraphs_persists_cascade() {
        ArticleReadingId articleId = ArticleReadingId.generate();
        List<ArticleParagraph> paragraphs = List.of(
                ArticleParagraph.create(articleId, "First paragraph content.", 0, ArticleSpeaker.AI),
                ArticleParagraph.create(articleId, "Second paragraph content.", 1, ArticleSpeaker.USER)
        );
        ArticleReading article = ArticleReading.reconstitute(
                articleId, userId, new ArticleTopic("Science"),
                ArticleLevel.C1, "Science Article", ArticleStatus.READY,
                paragraphs, 0, Instant.now());

        articleReadingRepository.save(article);

        Optional<ArticleReading> found = articleReadingRepository.findById(articleId);

        assertThat(found).isPresent();
        assertThat(found.get().paragraphs()).hasSize(2);
        assertThat(found.get().paragraphs().get(0).content()).isEqualTo("First paragraph content.");
        assertThat(found.get().paragraphs().get(0).speaker()).isEqualTo(ArticleSpeaker.AI);
        assertThat(found.get().paragraphs().get(1).orderIndex()).isEqualTo(1);
    }

    @Test
    void status_transitions_persist_correctly() {
        ArticleReading article = ArticleReading.create(userId, new ArticleTopic("Tech"), ArticleLevel.B1);
        articleReadingRepository.save(article);
        assertThat(articleReadingRepository.findById(article.id()).get().status())
                .isEqualTo(ArticleStatus.PENDING);

        ArticleReading processing = article.markProcessing();
        articleReadingRepository.save(processing);
        assertThat(articleReadingRepository.findById(article.id()).get().status())
                .isEqualTo(ArticleStatus.PROCESSING);

        ArticleReading ready = processing.markReady("Ready Title", List.of());
        articleReadingRepository.save(ready);
        assertThat(articleReadingRepository.findById(article.id()).get().status())
                .isEqualTo(ArticleStatus.READY);

        ArticleReading failed = article.markFailed();
        articleReadingRepository.save(failed);
        assertThat(articleReadingRepository.findById(article.id()).get().status())
                .isEqualTo(ArticleStatus.FAILED);
    }
}

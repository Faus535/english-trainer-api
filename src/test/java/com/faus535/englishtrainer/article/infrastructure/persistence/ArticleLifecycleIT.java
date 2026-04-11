package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.IntegrationTestBase;
import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAlreadyCompletedException;
import com.faus535.englishtrainer.article.domain.error.DuplicateMarkedWordException;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArticleLifecycleIT extends IntegrationTestBase {

    @Autowired
    private ArticleReadingRepository articleReadingRepository;

    @Autowired
    private ArticleQuestionRepository questionRepository;

    @Autowired
    private ArticleQuestionAnswerRepository answerRepository;

    @Autowired
    private ArticleMarkedWordRepository markedWordRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private UUID userId;
    private ArticleReading article;

    @BeforeEach
    void setUpArticle() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);
        userId = userProfile.id().value();

        ArticleReadingId articleId = ArticleReadingId.generate();
        List<ArticleParagraph> paragraphs = List.of(
                ArticleParagraph.create(articleId, "European leaders gathered in Brussels.", 0, ArticleSpeaker.AI),
                ArticleParagraph.create(articleId, "Critics argue the timeline is too aggressive.", 1, ArticleSpeaker.USER)
        );
        article = ArticleReading.reconstitute(
                articleId, userId, new ArticleTopic("Climate"),
                ArticleLevel.B2, "EU Climate Debate", ArticleStatus.READY,
                paragraphs, 0, 0, 0, Instant.now());
        articleReadingRepository.save(article);
    }

    @Test
    void marked_words_cascade_delete_with_article() throws DuplicateMarkedWordException {
        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "gathered"));
        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "aggressive"));

        assertThat(markedWordRepository.findByArticleIdAndUserId(article.id(), userId)).hasSize(2);

        articleReadingRepository.deleteById(article.id());

        assertThat(markedWordRepository.findByArticleIdAndUserId(article.id(), userId)).isEmpty();
    }

    @Test
    void questions_and_answers_cascade_delete_with_article() throws Exception {
        ArticleQuestion q1 = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(q1);
        answerRepository.save(ArticleQuestionAnswerMother.valid(q1.id()));

        assertThat(questionRepository.findByArticleReadingId(article.id())).hasSize(1);
        assertThat(answerRepository.findByQuestionId(q1.id())).isPresent();

        articleReadingRepository.deleteById(article.id());

        assertThat(questionRepository.findByArticleReadingId(article.id())).isEmpty();
        assertThat(answerRepository.findByQuestionId(q1.id())).isEmpty();
    }

    @Test
    void complete_with_xp_persists_correctly() throws ArticleAlreadyCompletedException {
        ArticleReading completed = article.complete(41);
        articleReadingRepository.save(completed);

        ArticleReading found = articleReadingRepository.findById(article.id()).orElseThrow();
        assertThat(found.status()).isEqualTo(ArticleStatus.COMPLETED);
        assertThat(found.xpEarned()).isEqualTo(41);
    }

    @Test
    void duplicate_marked_word_constraint_enforced() throws DuplicateMarkedWordException {
        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "gathered"));

        assertThatThrownBy(() ->
                markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "gathered")))
                .isInstanceOf(DuplicateMarkedWordException.class);
    }

    @Test
    void question_answer_uniqueness_constraint_enforced() throws Exception {
        ArticleQuestion question = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(question);
        answerRepository.save(ArticleQuestionAnswerMother.valid(question.id()));

        assertThatThrownBy(() ->
                answerRepository.save(ArticleQuestionAnswerMother.valid(question.id())))
                .isInstanceOf(com.faus535.englishtrainer.article.domain.error.QuestionAlreadyAnsweredException.class);
    }

    @Test
    void history_query_counts_words_and_answers_correctly() throws Exception {
        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "gathered"));
        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "debate"));

        ArticleQuestion q1 = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(q1);
        answerRepository.save(ArticleQuestionAnswerMother.valid(q1.id()));

        List<ArticleMarkedWord> words = markedWordRepository.findByArticleIdAndUserId(article.id(), userId);
        assertThat(words).hasSize(2);

        List<ArticleQuestion> questions = questionRepository.findByArticleReadingId(article.id());
        assertThat(questions).hasSize(1);

        assertThat(answerRepository.findByQuestionId(q1.id())).isPresent();
    }
}

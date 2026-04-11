package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleMarkedWordRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleQuestionAnswerRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleQuestionRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetArticleHistoryUseCaseTest {

    private InMemoryArticleReadingRepository readingRepository;
    private InMemoryArticleMarkedWordRepository markedWordRepository;
    private InMemoryArticleQuestionRepository questionRepository;
    private InMemoryArticleQuestionAnswerRepository answerRepository;
    private GetArticleHistoryUseCase useCase;

    @BeforeEach
    void setUp() {
        readingRepository = new InMemoryArticleReadingRepository();
        markedWordRepository = new InMemoryArticleMarkedWordRepository();
        questionRepository = new InMemoryArticleQuestionRepository();
        answerRepository = new InMemoryArticleQuestionAnswerRepository();
        useCase = new GetArticleHistoryUseCase(readingRepository, markedWordRepository,
                questionRepository, answerRepository);
    }

    @Test
    void shouldReturnArticlesWithWordAndQuestionCounts() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        readingRepository.save(article);

        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "debate"));
        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "policy"));

        ArticleQuestion question = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(question);
        answerRepository.save(ArticleQuestionAnswerMother.valid(question.id()));

        List<ArticleReadingSummary> result = useCase.execute(userId);

        assertEquals(1, result.size());
        ArticleReadingSummary summary = result.get(0);
        assertEquals(article.id().value(), summary.articleReadingId());
        assertEquals(2, summary.wordCount());
        assertEquals(1, summary.questionsAnswered());
    }

    @Test
    void shouldReturnEmptyListWhenNoArticles() {
        UUID userId = UUID.randomUUID();

        List<ArticleReadingSummary> result = useCase.execute(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnArticlesOrderedDescByCreatedAt() {
        UUID userId = UUID.randomUUID();
        Instant older = Instant.parse("2026-04-01T10:00:00Z");
        Instant newer = Instant.parse("2026-04-08T10:00:00Z");

        ArticleReading olderArticle = ArticleReading.reconstitute(
                ArticleReadingId.generate(), userId, new ArticleTopic("Old topic"),
                ArticleLevel.B1, "Old Article", ArticleStatus.IN_PROGRESS, List.of(), 0, 0, 0, older);
        ArticleReading newerArticle = ArticleReading.reconstitute(
                ArticleReadingId.generate(), userId, new ArticleTopic("New topic"),
                ArticleLevel.B2, "New Article", ArticleStatus.IN_PROGRESS, List.of(), 0, 0, 0, newer);

        readingRepository.save(olderArticle);
        readingRepository.save(newerArticle);

        List<ArticleReadingSummary> result = useCase.execute(userId);

        assertEquals(2, result.size());
        assertEquals("New Article", result.get(0).title());
        assertEquals("Old Article", result.get(1).title());
    }
}

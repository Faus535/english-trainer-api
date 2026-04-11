package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.ArticleMarkedWord;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.ArticleMarkedWordMother;
import com.faus535.englishtrainer.article.infrastructure.FailingStubArticleAiPort;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleMarkedWordRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import com.faus535.englishtrainer.article.infrastructure.StubArticleAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnrichMarkedWordAsyncServiceTest {

    private InMemoryArticleMarkedWordRepository markedWordRepository;
    private InMemoryArticleReadingRepository articleReadingRepository;

    @BeforeEach
    void setUp() {
        markedWordRepository = new InMemoryArticleMarkedWordRepository();
        articleReadingRepository = new InMemoryArticleReadingRepository();
    }

    @SuppressWarnings("unchecked")
    private TransactionTemplate noOpTransactionTemplate() {
        TransactionTemplate template = mock(TransactionTemplate.class);
        doAnswer(invocation -> {
            Consumer<org.springframework.transaction.TransactionStatus> action =
                    invocation.getArgument(0, Consumer.class);
            action.accept(null);
            return null;
        }).when(template).executeWithoutResult(any());
        return template;
    }

    @Test
    void shouldEnrichMarkedWordWhenAiSucceeds() throws Exception {
        EnrichMarkedWordAsyncService service = new EnrichMarkedWordAsyncService(
                markedWordRepository, articleReadingRepository,
                new StubArticleAiPort(), noOpTransactionTemplate());

        ArticleReadingId articleId = new ArticleReadingId(UUID.randomUUID());
        UUID userId = UUID.randomUUID();
        ArticleMarkedWord word = ArticleMarkedWordMother.withWord(articleId, userId, "spark debate");
        markedWordRepository.save(word);

        service.enrich(word.id().value(), articleId.value(), "spark debate", "Context sentence.");

        ArticleMarkedWord enriched = markedWordRepository.findById(word.id()).orElseThrow();
        assertNotNull(enriched.definition());
        assertNotNull(enriched.phonetics());
        assertNotNull(enriched.synonyms());
        assertFalse(enriched.synonyms().isEmpty());
        assertNotNull(enriched.exampleSentence());
        assertNotNull(enriched.partOfSpeech());
    }

    @Test
    void shouldLogAndContinueWhenMarkedWordNotFound() {
        EnrichMarkedWordAsyncService service = new EnrichMarkedWordAsyncService(
                markedWordRepository, articleReadingRepository,
                new StubArticleAiPort(), noOpTransactionTemplate());

        assertDoesNotThrow(() ->
                service.enrich(UUID.randomUUID(), UUID.randomUUID(), "spark debate", "Context."));
    }

    @Test
    void shouldLogAndContinueWhenAiFails() throws Exception {
        EnrichMarkedWordAsyncService service = new EnrichMarkedWordAsyncService(
                markedWordRepository, articleReadingRepository,
                new FailingStubArticleAiPort(), noOpTransactionTemplate());

        ArticleReadingId articleId = new ArticleReadingId(UUID.randomUUID());
        UUID userId = UUID.randomUUID();
        ArticleMarkedWord word = ArticleMarkedWordMother.withWord(articleId, userId, "spark debate");
        markedWordRepository.save(word);

        assertDoesNotThrow(() ->
                service.enrich(word.id().value(), articleId.value(), "spark debate", "Context."));

        ArticleMarkedWord unchanged = markedWordRepository.findById(word.id()).orElseThrow();
        assertNull(unchanged.definition());
        assertNull(unchanged.phonetics());
    }

    @Test
    void shouldPreserveOriginalFieldsWhenEnriching() throws Exception {
        EnrichMarkedWordAsyncService service = new EnrichMarkedWordAsyncService(
                markedWordRepository, articleReadingRepository,
                new StubArticleAiPort(), noOpTransactionTemplate());

        ArticleReadingId articleId = new ArticleReadingId(UUID.randomUUID());
        UUID userId = UUID.randomUUID();
        ArticleMarkedWord word = ArticleMarkedWordMother.withWord(articleId, userId, "spark debate");
        markedWordRepository.save(word);

        service.enrich(word.id().value(), articleId.value(), "spark debate", "Context sentence.");

        ArticleMarkedWord enriched = markedWordRepository.findById(word.id()).orElseThrow();
        assertEquals(word.wordOrPhrase(), enriched.wordOrPhrase());
        assertEquals(word.translation(), enriched.translation());
        assertEquals(word.createdAt(), enriched.createdAt());
        assertEquals(word.id(), enriched.id());
    }
}

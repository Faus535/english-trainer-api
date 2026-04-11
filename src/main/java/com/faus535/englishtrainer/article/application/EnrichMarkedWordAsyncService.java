package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.ArticleAiPort;
import com.faus535.englishtrainer.article.domain.ArticleMarkedWord;
import com.faus535.englishtrainer.article.domain.ArticleMarkedWordId;
import com.faus535.englishtrainer.article.domain.ArticleMarkedWordRepository;
import com.faus535.englishtrainer.article.domain.ArticleReading;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.ArticleReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Service
public class EnrichMarkedWordAsyncService {

    private static final Logger log = LoggerFactory.getLogger(EnrichMarkedWordAsyncService.class);

    private final ArticleMarkedWordRepository markedWordRepository;
    private final ArticleReadingRepository articleReadingRepository;
    private final ArticleAiPort aiPort;
    private final TransactionTemplate transactionTemplate;

    public EnrichMarkedWordAsyncService(ArticleMarkedWordRepository markedWordRepository,
                                        ArticleReadingRepository articleReadingRepository,
                                        ArticleAiPort aiPort,
                                        TransactionTemplate transactionTemplate) {
        this.markedWordRepository = markedWordRepository;
        this.articleReadingRepository = articleReadingRepository;
        this.aiPort = aiPort;
        this.transactionTemplate = transactionTemplate;
    }

    @Async("articleAsyncExecutor")
    public void enrich(UUID markedWordId, UUID articleReadingId, String wordOrPhrase, String contextSentence) {
        try {
            String articleParagraph = resolveArticleParagraph(articleReadingId, contextSentence);
            ArticleAiPort.WordEnrichmentResult enrichment = aiPort.enrichWord(wordOrPhrase, contextSentence, articleParagraph);
            transactionTemplate.executeWithoutResult(status ->
                    markedWordRepository.findById(new ArticleMarkedWordId(markedWordId))
                            .ifPresentOrElse(word -> {
                                ArticleMarkedWord enriched = word.enrich(
                                        enrichment.definition(), enrichment.phonetics(),
                                        enrichment.synonyms(), enrichment.exampleSentence(),
                                        enrichment.partOfSpeech());
                                markedWordRepository.update(enriched);
                                log.info("Enriched marked word '{}' (id={})", wordOrPhrase, markedWordId);
                            }, () -> log.warn("Marked word not found for enrichment: {}", markedWordId))
            );
        } catch (Exception e) {
            log.error("Failed to enrich marked word '{}' (id={}): {}", wordOrPhrase, markedWordId, e.getMessage(), e);
        }
    }

    private String resolveArticleParagraph(UUID articleReadingId, String contextSentence) {
        try {
            return articleReadingRepository.findById(new ArticleReadingId(articleReadingId))
                    .map(ArticleReading::paragraphs)
                    .filter(paragraphs -> !paragraphs.isEmpty())
                    .map(paragraphs -> paragraphs.stream()
                            .map(p -> p.content())
                            .findFirst()
                            .orElse(contextSentence))
                    .orElse(contextSentence);
        } catch (Exception e) {
            log.debug("Could not resolve article paragraph for enrichment, using context sentence", e);
            return contextSentence;
        }
    }
}

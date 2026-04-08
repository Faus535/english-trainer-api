package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GenerateArticleQuestionsAsyncService {

    private static final Logger log = LoggerFactory.getLogger(GenerateArticleQuestionsAsyncService.class);

    private final ArticleReadingRepository readingRepository;
    private final ArticleQuestionRepository questionRepository;
    private final ArticleAiPort aiPort;
    private final TransactionTemplate transactionTemplate;

    public GenerateArticleQuestionsAsyncService(ArticleReadingRepository readingRepository,
                                                  ArticleQuestionRepository questionRepository,
                                                  ArticleAiPort aiPort,
                                                  TransactionTemplate transactionTemplate) {
        this.readingRepository = readingRepository;
        this.questionRepository = questionRepository;
        this.aiPort = aiPort;
        this.transactionTemplate = transactionTemplate;
    }

    @Async("articleAsyncExecutor")
    public void generateQuestions(UUID articleReadingId) {
        try {
            ArticleReading article = readingRepository.findById(new ArticleReadingId(articleReadingId))
                    .orElse(null);

            if (article == null) {
                log.warn("Article not found for question generation: {}", articleReadingId);
                return;
            }

            String paragraphsText = article.paragraphs().stream()
                    .map(ArticleParagraph::content)
                    .collect(Collectors.joining("\n\n"));

            ArticleAiPort.ArticleQuestionsResult result = aiPort.generateQuestions(
                    paragraphsText, article.level().value());

            List<ArticleQuestion> questions = result.questions().stream()
                    .map(q -> ArticleQuestion.create(
                            article.id(), q.questionText(), q.orderIndex(), q.hintText()))
                    .toList();

            transactionTemplate.executeWithoutResult(status ->
                    questions.forEach(questionRepository::save));

            log.info("Generated {} questions for article {} (level: {}, style: {})",
                    questions.size(), articleReadingId, article.level().value(),
                    article.level().questionStyle());

        } catch (Exception e) {
            log.error("Failed to generate questions for article {}", articleReadingId, e);
        }
    }
}

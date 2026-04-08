package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UseCase
public class GetArticleHistoryUseCase {

    private final ArticleReadingRepository readingRepository;
    private final ArticleMarkedWordRepository markedWordRepository;
    private final ArticleQuestionRepository questionRepository;
    private final ArticleQuestionAnswerRepository answerRepository;

    GetArticleHistoryUseCase(ArticleReadingRepository readingRepository,
                             ArticleMarkedWordRepository markedWordRepository,
                             ArticleQuestionRepository questionRepository,
                             ArticleQuestionAnswerRepository answerRepository) {
        this.readingRepository = readingRepository;
        this.markedWordRepository = markedWordRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @Transactional(readOnly = true)
    public List<ArticleReadingSummary> execute(UUID userId) {
        List<ArticleReading> articles = readingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<ArticleReadingSummary> summaries = new ArrayList<>();

        for (ArticleReading article : articles) {
            int wordCount = markedWordRepository
                    .findByArticleIdAndUserId(article.id(), userId).size();

            List<ArticleQuestion> questions = questionRepository
                    .findByArticleReadingId(article.id());
            int questionsAnswered = 0;
            for (ArticleQuestion q : questions) {
                if (answerRepository.findByQuestionId(q.id()).isPresent()) {
                    questionsAnswered++;
                }
            }

            summaries.add(new ArticleReadingSummary(
                    article.id().value(),
                    article.title(),
                    article.level().value(),
                    article.topic().value(),
                    article.status().value(),
                    article.createdAt(),
                    wordCount,
                    questionsAnswered
            ));
        }

        return summaries;
    }
}

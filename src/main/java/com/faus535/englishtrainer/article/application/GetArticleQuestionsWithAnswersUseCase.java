package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@UseCase
public class GetArticleQuestionsWithAnswersUseCase {

    private final ArticleReadingRepository readingRepository;
    private final ArticleQuestionRepository questionRepository;
    private final ArticleQuestionAnswerRepository answerRepository;

    GetArticleQuestionsWithAnswersUseCase(ArticleReadingRepository readingRepository,
                                          ArticleQuestionRepository questionRepository,
                                          ArticleQuestionAnswerRepository answerRepository) {
        this.readingRepository = readingRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @Transactional(readOnly = true)
    public List<QuestionWithAnswer> execute(UUID userId, ArticleReadingId articleReadingId)
            throws ArticleNotFoundException, ArticleAccessDeniedException {
        ArticleReading article = readingRepository.findById(articleReadingId)
                .orElseThrow(() -> new ArticleNotFoundException(articleReadingId));

        if (!article.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(articleReadingId);
        }

        List<ArticleQuestion> questions = questionRepository.findByArticleReadingId(articleReadingId);
        List<QuestionWithAnswer> result = new ArrayList<>();

        for (ArticleQuestion question : questions) {
            Optional<ArticleQuestionAnswer> answer = answerRepository.findByQuestionId(question.id());
            result.add(new QuestionWithAnswer(
                    question.id().value(),
                    question.questionText(),
                    answer.isPresent(),
                    answer.map(ArticleQuestionAnswer::userAnswer).orElse(null)
            ));
        }

        return result;
    }
}

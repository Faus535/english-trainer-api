package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.List;

@UseCase
public class GenerateQuestionsUseCase {

    private final ArticleAiPort aiPort;
    private final ArticleQuestionRepository questionRepository;

    GenerateQuestionsUseCase(ArticleAiPort aiPort, ArticleQuestionRepository questionRepository) {
        this.aiPort = aiPort;
        this.questionRepository = questionRepository;
    }

    public List<ArticleQuestion> execute(ArticleReadingId articleId, String articleText, String level)
            throws ArticleAiException {
        ArticleAiPort.ArticleQuestionsResult result = aiPort.generateQuestions(articleText, level);

        List<ArticleQuestion> questions = result.questions().stream()
                .map(q -> ArticleQuestion.create(articleId, q.questionText(), q.orderIndex(), q.hintText()))
                .toList();

        questions.forEach(questionRepository::save);
        return questions;
    }
}

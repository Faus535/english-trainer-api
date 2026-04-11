package com.faus535.englishtrainer.article.infrastructure;

import com.faus535.englishtrainer.article.domain.ArticleAiPort;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;

public class FailingStubArticleAiPort implements ArticleAiPort {

    @Override
    public ArticleGenerateResult generateArticle(String topic, String level) throws ArticleAiException {
        throw new ArticleAiException("Claude API unavailable");
    }

    @Override
    public ArticleTranslationResult translateWord(String wordOrPhrase, String contextSentence) throws ArticleAiException {
        throw new ArticleAiException("Claude API unavailable");
    }

    @Override
    public ArticleQuestionsResult generateQuestions(String articleText, String level) throws ArticleAiException {
        throw new ArticleAiException("Claude API unavailable");
    }

    @Override
    public ArticleAnswerCorrectionResult correctAnswer(String question, String userAnswer, String articleText) throws ArticleAiException {
        throw new ArticleAiException("Claude API unavailable");
    }

    @Override
    public PreReadingResult generatePreReading(String articleText, String level) throws ArticleAiException {
        throw new ArticleAiException("Claude API unavailable");
    }
}

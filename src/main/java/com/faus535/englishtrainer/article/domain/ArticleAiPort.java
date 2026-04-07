package com.faus535.englishtrainer.article.domain;

import com.faus535.englishtrainer.article.domain.error.ArticleAiException;

import java.util.List;

public interface ArticleAiPort {

    ArticleGenerateResult generateArticle(String topic, String level) throws ArticleAiException;

    ArticleTranslationResult translateWord(String wordOrPhrase, String contextSentence) throws ArticleAiException;

    ArticleQuestionsResult generateQuestions(String articleText, String level) throws ArticleAiException;

    ArticleAnswerCorrectionResult correctAnswer(String question, String userAnswer, String articleText) throws ArticleAiException;

    record ArticleGenerateResult(String title, List<ArticleParagraphData> paragraphs) {}

    record ArticleParagraphData(String content, int orderIndex, String speaker) {}

    record ArticleTranslationResult(String translation) {}

    record ArticleQuestionsResult(List<ArticleQuestionData> questions) {}

    record ArticleQuestionData(String questionText, int orderIndex, String hintText) {}

    record ArticleAnswerCorrectionResult(boolean isContentCorrect, String grammarFeedback,
                                         String styleFeedback, String correctionSummary) {}
}

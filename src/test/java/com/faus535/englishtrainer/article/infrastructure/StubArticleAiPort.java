package com.faus535.englishtrainer.article.infrastructure;

import com.faus535.englishtrainer.article.domain.ArticleAiPort;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;

import java.util.List;

public class StubArticleAiPort implements ArticleAiPort {

    @Override
    public ArticleGenerateResult generateArticle(String topic, String level) throws ArticleAiException {
        return new ArticleGenerateResult(
                "EU's New Climate Targets Spark Debate",
                List.of(
                        new ArticleParagraphData("European leaders gathered in Brussels...", 0, "AI"),
                        new ArticleParagraphData("Critics argue that the timeline...", 1, "USER"),
                        new ArticleParagraphData("The European Commission responded...", 2, "AI")
                )
        );
    }

    @Override
    public ArticleTranslationResult translateWord(String wordOrPhrase, String contextSentence) throws ArticleAiException {
        return new ArticleTranslationResult("traducción de prueba", "brief English definition");
    }

    @Override
    public ArticleQuestionsResult generateQuestions(String articleText, String level) throws ArticleAiException {
        return new ArticleQuestionsResult(List.of(
                new ArticleQuestionData("What is the main argument of the article?", 0, "Think about the central theme."),
                new ArticleQuestionData("What do critics say about the timeline?", 1, "Look at paragraph 2.")
        ));
    }

    @Override
    public ArticleAnswerCorrectionResult correctAnswer(String question, String userAnswer, String articleText) throws ArticleAiException {
        return new ArticleAnswerCorrectionResult(true, "Good grammar.", "Clear style.", "Well answered.");
    }

    @Override
    public PreReadingResult generatePreReading(String articleText, String level) throws ArticleAiException {
        return new PreReadingResult(
                List.of(
                        new KeyWordData("resilience", "resiliencia", "the ability to recover quickly from difficulties"),
                        new KeyWordData("debate", "debate", "a formal discussion on a particular topic")
                ),
                "What do you think this article will discuss about climate change?"
        );
    }

    @Override
    public WordEnrichmentResult enrichWord(String wordOrPhrase, String contextSentence, String articleParagraph)
            throws ArticleAiException {
        return new WordEnrichmentResult(
                "To cause or provoke a discussion or argument",
                "/spɑːrk dɪˈbeɪt/",
                List.of("ignite discussion", "stir controversy", "provoke argument"),
                "The new reforms are likely to spark debate in parliament.",
                "verb phrase"
        );
    }
}

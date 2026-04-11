package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.*;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
public class SubmitAnswerUseCase {

    private final ArticleReadingRepository articleReadingRepository;
    private final ArticleQuestionRepository questionRepository;
    private final ArticleQuestionAnswerRepository questionAnswerRepository;
    private final ArticleAiPort aiPort;

    SubmitAnswerUseCase(ArticleReadingRepository articleReadingRepository,
                        ArticleQuestionRepository questionRepository,
                        ArticleQuestionAnswerRepository questionAnswerRepository,
                        ArticleAiPort aiPort) {
        this.articleReadingRepository = articleReadingRepository;
        this.questionRepository = questionRepository;
        this.questionAnswerRepository = questionAnswerRepository;
        this.aiPort = aiPort;
    }

    public ArticleQuestionAnswer execute(UUID userId, ArticleReadingId articleId,
                                          ArticleQuestionId questionId, String answer)
            throws ArticleNotFoundException, ArticleAccessDeniedException,
            ArticleQuestionNotFoundException, QuestionAlreadyAnsweredException,
            AnswerTooShortException, ArticleAiException {
        ArticleReading article = articleReadingRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        if (!article.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(articleId);
        }

        ArticleQuestion question = questionRepository.findById(questionId)
                .filter(q -> q.articleReadingId().equals(articleId))
                .orElseThrow(() -> new ArticleQuestionNotFoundException(questionId));

        if (questionAnswerRepository.findByQuestionId(questionId).isPresent()) {
            throw new QuestionAlreadyAnsweredException(questionId);
        }

        String articleText = article.paragraphs().stream()
                .map(ArticleParagraph::content)
                .collect(Collectors.joining("\n\n"));

        ArticleAiPort.ArticleAnswerCorrectionResult correction =
                aiPort.correctAnswer(question.questionText(), answer, articleText);

        ArticleQuestionAnswer questionAnswer = ArticleQuestionAnswer.create(
                questionId, answer, article.level().minWords(), correction.isContentCorrect(),
                correction.grammarFeedback(), correction.styleFeedback(), correction.correctionSummary());

        questionAnswerRepository.save(questionAnswer);
        return questionAnswer;
    }
}

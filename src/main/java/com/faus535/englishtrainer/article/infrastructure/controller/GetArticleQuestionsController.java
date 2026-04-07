package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.GetArticleUseCase;
import com.faus535.englishtrainer.article.application.GetQuestionHintUseCase;
import com.faus535.englishtrainer.article.domain.ArticleQuestion;
import com.faus535.englishtrainer.article.domain.ArticleQuestionRepository;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class GetArticleQuestionsController {

    private final GetArticleUseCase getArticleUseCase;
    private final ArticleQuestionRepository questionRepository;

    GetArticleQuestionsController(GetArticleUseCase getArticleUseCase,
                                   ArticleQuestionRepository questionRepository) {
        this.getArticleUseCase = getArticleUseCase;
        this.questionRepository = questionRepository;
    }

    record QuestionResponse(UUID id, String questionText, int orderIndex, int minWords) {
        static QuestionResponse from(ArticleQuestion q) {
            return new QuestionResponse(q.id().value(), q.questionText(), q.orderIndex(), q.minWords());
        }
    }

    @GetMapping("/api/article/{id}/questions")
    ResponseEntity<List<QuestionResponse>> handle(@PathVariable UUID id, Authentication authentication)
            throws ArticleNotFoundException, ArticleAccessDeniedException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        getArticleUseCase.execute(userId, new ArticleReadingId(id));

        List<QuestionResponse> questions = questionRepository
                .findByArticleReadingId(new ArticleReadingId(id))
                .stream().map(QuestionResponse::from).toList();

        return ResponseEntity.ok(questions);
    }
}

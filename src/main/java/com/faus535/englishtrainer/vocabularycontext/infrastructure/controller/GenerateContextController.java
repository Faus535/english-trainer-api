package com.faus535.englishtrainer.vocabularycontext.infrastructure.controller;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabularycontext.application.GenerateContextSentencesUseCase;
import com.faus535.englishtrainer.vocabularycontext.domain.VocabularyContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
class GenerateContextController {

    private final GenerateContextSentencesUseCase useCase;

    GenerateContextController(GenerateContextSentencesUseCase useCase) {
        this.useCase = useCase;
    }

    record ContextResponse(String id, String vocabularyId, String level, String sentencesJson, Instant generatedAt) {}

    @PostMapping("/api/vocabulary/{wordId}/context")
    ResponseEntity<ContextResponse> handle(
            @PathVariable String wordId,
            @RequestParam(defaultValue = "a2") String level,
            @RequestParam(required = false) String word) throws Exception {

        VocabEntryId vocabularyId = VocabEntryId.fromString(wordId);
        String wordForGeneration = (word != null && !word.isBlank()) ? word : wordId;

        VocabularyContext context = useCase.execute(vocabularyId, wordForGeneration, level.toLowerCase());

        return ResponseEntity.ok(new ContextResponse(
                context.id().value().toString(),
                context.vocabularyId().value().toString(),
                context.level(),
                context.sentencesJson(),
                context.generatedAt()
        ));
    }
}

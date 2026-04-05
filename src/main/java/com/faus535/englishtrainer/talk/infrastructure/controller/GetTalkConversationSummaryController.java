package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.application.GetTalkConversationSummaryUseCase;
import com.faus535.englishtrainer.talk.domain.TalkEvaluation;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetTalkConversationSummaryController {

    private final GetTalkConversationSummaryUseCase useCase;

    GetTalkConversationSummaryController(GetTalkConversationSummaryUseCase useCase) {
        this.useCase = useCase;
    }

    record SummaryResponse(String summary, TalkEvaluation evaluation, int turnCount, int errorCount) {}

    @GetMapping("/api/talk/conversations/{id}/summary")
    ResponseEntity<SummaryResponse> handle(@PathVariable UUID id)
            throws TalkConversationNotFoundException {

        var result = useCase.execute(id);

        return ResponseEntity.ok(new SummaryResponse(
                result.summary(), result.evaluation(), result.turnCount(), result.errorCount()));
    }
}

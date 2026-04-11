package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.application.GetTalkConversationSummaryUseCase;
import com.faus535.englishtrainer.talk.application.TalkConversationSummaryResult;
import com.faus535.englishtrainer.talk.domain.TalkEvaluation;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetTalkConversationSummaryController {

    private final GetTalkConversationSummaryUseCase useCase;

    GetTalkConversationSummaryController(GetTalkConversationSummaryUseCase useCase) {
        this.useCase = useCase;
    }

    record FullSummaryResponse(String mode, String summary, TalkEvaluation evaluation,
                                int turnCount, int errorCount) {}

    record QuickSummaryResponse(String mode, boolean taskCompleted,
                                 List<String> top3Corrections, String encouragementNote) {}

    @GetMapping("/api/talk/conversations/{id}/summary")
    ResponseEntity<?> handle(@PathVariable UUID id) throws TalkConversationNotFoundException {

        TalkConversationSummaryResult result = useCase.execute(id);

        return switch (result) {
            case TalkConversationSummaryResult.FullSummaryResult r ->
                    ResponseEntity.ok(new FullSummaryResponse("FULL", r.summary(), r.evaluation(),
                            r.turnCount(), r.errorCount()));
            case TalkConversationSummaryResult.QuickSummaryResult r ->
                    ResponseEntity.ok(new QuickSummaryResponse("QUICK", r.taskCompleted(),
                            r.top3Corrections(), r.encouragementNote()));
        };
    }
}

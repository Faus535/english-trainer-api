package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.application.EndTalkConversationUseCase;
import com.faus535.englishtrainer.talk.domain.TalkEvaluation;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationAlreadyEndedException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class EndTalkConversationController {

    private final EndTalkConversationUseCase useCase;

    EndTalkConversationController(EndTalkConversationUseCase useCase) {
        this.useCase = useCase;
    }

    record EndTalkResponse(String summary, TalkEvaluation evaluation, int turnCount, int errorCount) {}

    @PostMapping("/api/talk/conversations/{id}/end")
    ResponseEntity<EndTalkResponse> handle(@PathVariable UUID id)
            throws TalkConversationNotFoundException, TalkConversationAlreadyEndedException, TalkAiException {

        var result = useCase.execute(id);

        return ResponseEntity.ok(new EndTalkResponse(
                result.summary(), result.evaluation(), result.turnCount(), result.errorCount()));
    }
}

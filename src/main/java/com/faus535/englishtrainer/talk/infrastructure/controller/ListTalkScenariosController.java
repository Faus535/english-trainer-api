package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.application.ListTalkScenariosUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class ListTalkScenariosController {

    private final ListTalkScenariosUseCase useCase;

    ListTalkScenariosController(ListTalkScenariosUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/talk/scenarios")
    ResponseEntity<List<TalkScenarioResponse>> handle(
            @RequestParam(required = false) String level) {

        var scenarios = useCase.execute(level).stream()
                .map(TalkScenarioResponse::from)
                .toList();

        return ResponseEntity.ok(scenarios);
    }
}

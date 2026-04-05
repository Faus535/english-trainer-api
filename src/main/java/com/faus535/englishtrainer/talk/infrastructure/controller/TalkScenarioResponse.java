package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.domain.TalkScenario;

record TalkScenarioResponse(
        String id,
        String title,
        String description,
        String category,
        String cefrLevel,
        int difficultyOrder
) {

    static TalkScenarioResponse from(TalkScenario scenario) {
        return new TalkScenarioResponse(
                scenario.id().value().toString(),
                scenario.title(),
                scenario.description(),
                scenario.category(),
                scenario.cefrLevel().value(),
                scenario.difficultyOrder()
        );
    }
}

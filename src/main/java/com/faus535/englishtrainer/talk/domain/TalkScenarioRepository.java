package com.faus535.englishtrainer.talk.domain;

import java.util.List;
import java.util.Optional;

public interface TalkScenarioRepository {

    List<TalkScenario> findAll();

    List<TalkScenario> findByLevel(TalkLevel level);

    Optional<TalkScenario> findById(TalkScenarioId id);
}

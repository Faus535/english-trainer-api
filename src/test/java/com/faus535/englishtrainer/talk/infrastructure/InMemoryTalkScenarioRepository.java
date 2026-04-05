package com.faus535.englishtrainer.talk.infrastructure;

import com.faus535.englishtrainer.talk.domain.*;

import java.util.*;

public class InMemoryTalkScenarioRepository implements TalkScenarioRepository {

    private final Map<UUID, TalkScenario> store = new LinkedHashMap<>();

    public void add(TalkScenario scenario) {
        store.put(scenario.id().value(), scenario);
    }

    @Override
    public List<TalkScenario> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<TalkScenario> findByLevel(TalkLevel level) {
        return store.values().stream()
                .filter(s -> s.cefrLevel().value().equals(level.value()))
                .toList();
    }

    @Override
    public Optional<TalkScenario> findById(TalkScenarioId id) {
        return Optional.ofNullable(store.get(id.value()));
    }
}

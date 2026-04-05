package com.faus535.englishtrainer.talk.infrastructure.persistence;

import com.faus535.englishtrainer.talk.domain.TalkLevel;
import com.faus535.englishtrainer.talk.domain.TalkScenario;
import com.faus535.englishtrainer.talk.domain.TalkScenarioId;
import com.faus535.englishtrainer.talk.domain.TalkScenarioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaTalkScenarioRepositoryAdapter implements TalkScenarioRepository {

    private final JpaTalkScenarioRepository jpaRepository;

    JpaTalkScenarioRepositoryAdapter(JpaTalkScenarioRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TalkScenario> findAll() {
        return jpaRepository.findAllByOrderByDifficultyOrderAsc().stream()
                .map(TalkScenarioEntity::toAggregate)
                .toList();
    }

    @Override
    public List<TalkScenario> findByLevel(TalkLevel level) {
        return jpaRepository.findByCefrLevelOrderByDifficultyOrderAsc(level.value()).stream()
                .map(TalkScenarioEntity::toAggregate)
                .toList();
    }

    @Override
    public Optional<TalkScenario> findById(TalkScenarioId id) {
        return jpaRepository.findById(id.value())
                .map(TalkScenarioEntity::toAggregate);
    }
}

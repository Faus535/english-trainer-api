package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.TalkLevel;
import com.faus535.englishtrainer.talk.domain.TalkScenario;
import com.faus535.englishtrainer.talk.domain.TalkScenarioRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class ListTalkScenariosUseCase {

    private final TalkScenarioRepository repository;

    public ListTalkScenariosUseCase(TalkScenarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TalkScenario> execute(String level) {
        if (level == null || level.isBlank()) {
            return repository.findAll();
        }
        return repository.findByLevel(new TalkLevel(level));
    }
}

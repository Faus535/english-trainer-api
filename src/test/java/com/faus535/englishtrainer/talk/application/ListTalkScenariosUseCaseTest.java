package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.TalkScenario;
import com.faus535.englishtrainer.talk.domain.TalkScenarioMother;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListTalkScenariosUseCaseTest {

    private InMemoryTalkScenarioRepository repository;
    private ListTalkScenariosUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTalkScenarioRepository();
        useCase = new ListTalkScenariosUseCase(repository);
    }

    @Test
    void returnsScenariosForLevel() {
        repository.add(TalkScenarioMother.coffeeShop());
        repository.add(TalkScenarioMother.jobInterview());

        List<TalkScenario> result = useCase.execute("b1");

        assertEquals(1, result.size());
        assertEquals("Job Interview", result.getFirst().title());
    }

    @Test
    void returnsAllWhenNoLevel() {
        repository.add(TalkScenarioMother.coffeeShop());
        repository.add(TalkScenarioMother.jobInterview());

        List<TalkScenario> result = useCase.execute(null);

        assertEquals(2, result.size());
    }

    @Test
    void returnsEmptyWhenNoMatch() {
        repository.add(TalkScenarioMother.coffeeShop());

        List<TalkScenario> result = useCase.execute("c1");

        assertTrue(result.isEmpty());
    }
}

package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import com.faus535.englishtrainer.talk.domain.error.TalkMaxConversationsExceededException;
import com.faus535.englishtrainer.talk.domain.TalkScenarioMother;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkConversationRepository;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkScenarioRepository;
import com.faus535.englishtrainer.talk.infrastructure.StubTalkAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class StartTalkConversationUseCaseTest {

    private InMemoryTalkConversationRepository conversationRepository;
    private InMemoryTalkScenarioRepository scenarioRepository;
    private StubTalkAiPort aiPort;
    private StartTalkConversationUseCase useCase;

    @BeforeEach
    void setUp() {
        conversationRepository = new InMemoryTalkConversationRepository();
        scenarioRepository = new InMemoryTalkScenarioRepository();
        aiPort = new StubTalkAiPort();
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        useCase = new StartTalkConversationUseCase(conversationRepository, scenarioRepository, aiPort, publisher);
    }

    @Test
    void startsConversationWithGreeting() throws Exception {
        TalkScenario scenario = TalkScenarioMother.coffeeShop();
        scenarioRepository.add(scenario);
        UUID userId = UUID.randomUUID();

        TalkConversation result = useCase.execute(userId, scenario.id().value(), "a2");

        assertEquals(TalkStatus.ACTIVE, result.status());
        assertEquals(1, result.messages().size());
        assertEquals("assistant", result.messages().getFirst().role());
    }

    @Test
    void throwsWhenActiveConversationExists() throws Exception {
        TalkScenario scenario = TalkScenarioMother.coffeeShop();
        scenarioRepository.add(scenario);
        UUID userId = UUID.randomUUID();

        useCase.execute(userId, scenario.id().value(), "a2");

        assertThrows(TalkMaxConversationsExceededException.class,
                () -> useCase.execute(userId, scenario.id().value(), "a2"));
    }
}

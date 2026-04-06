package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.infrastructure.FailingStubImmerseAiPort;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseExerciseRepository;
import com.faus535.englishtrainer.immerse.infrastructure.StubImmerseAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessImmerseContentAsyncServiceTest {

    private InMemoryImmerseContentRepository contentRepository;
    private InMemoryImmerseExerciseRepository exerciseRepository;

    @BeforeEach
    void setUp() {
        contentRepository = new InMemoryImmerseContentRepository();
        exerciseRepository = new InMemoryImmerseExerciseRepository();
    }

    @SuppressWarnings("unchecked")
    private TransactionTemplate noOpTransactionTemplate() {
        TransactionTemplate template = mock(TransactionTemplate.class);
        doAnswer(invocation -> {
            Consumer<org.springframework.transaction.TransactionStatus> action =
                    invocation.getArgument(0, Consumer.class);
            action.accept(null);
            return null;
        }).when(template).executeWithoutResult(any());
        return template;
    }

    @Test
    void processesPendingContentSuccessfully() {
        ProcessImmerseContentAsyncService service = new ProcessImmerseContentAsyncService(
                contentRepository, exerciseRepository, new StubImmerseAiPort(), noOpTransactionTemplate());

        ImmerseContent pending = ImmerseContent.generate(UUID.randomUUID(), ContentType.TEXT, "b1", "city life");
        contentRepository.save(pending);

        service.process(pending.id().value(), ContentType.TEXT, "b1", "city life");

        ImmerseContent result = contentRepository.findById(pending.id()).orElseThrow();
        assertEquals(ImmerseContentStatus.PROCESSED, result.status());
        assertEquals("A Day in the City", result.title());
        assertFalse(exerciseRepository.findByContentId(pending.id()).isEmpty());
    }

    @Test
    void marksContentFailedWhenAiThrows() {
        ProcessImmerseContentAsyncService service = new ProcessImmerseContentAsyncService(
                contentRepository, exerciseRepository, new FailingStubImmerseAiPort(), noOpTransactionTemplate());

        ImmerseContent pending = ImmerseContent.generate(UUID.randomUUID(), ContentType.TEXT, "b1", "topic");
        contentRepository.save(pending);

        service.process(pending.id().value(), ContentType.TEXT, "b1", "topic");

        ImmerseContent result = contentRepository.findById(pending.id()).orElseThrow();
        assertEquals(ImmerseContentStatus.FAILED, result.status());
    }

    @Test
    void doesNothingWhenContentNotFound() {
        ProcessImmerseContentAsyncService service = new ProcessImmerseContentAsyncService(
                contentRepository, exerciseRepository, new StubImmerseAiPort(), noOpTransactionTemplate());

        assertDoesNotThrow(() ->
                service.process(UUID.randomUUID(), ContentType.TEXT, "b1", "topic"));
    }
}

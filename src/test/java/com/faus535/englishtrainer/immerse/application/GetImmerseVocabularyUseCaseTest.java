package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentMother;
import com.faus535.englishtrainer.immerse.domain.VocabularyItem;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetImmerseVocabularyUseCaseTest {

    private InMemoryImmerseContentRepository repository;
    private GetImmerseVocabularyUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryImmerseContentRepository();
        useCase = new GetImmerseVocabularyUseCase(repository);
    }

    @Test
    void shouldReturnVocabularyForExistingContent() throws Exception {
        ImmerseContent content = ImmerseContentMother.processed();
        repository.save(content);

        List<VocabularyItem> result = useCase.execute(content.id().value());

        assertFalse(result.isEmpty());
        assertEquals(content.extractedVocabulary().size(), result.size());
    }

    @Test
    void shouldThrowImmerseContentNotFoundExceptionWhenMissing() {
        UUID randomId = UUID.randomUUID();
        assertThrows(ImmerseContentNotFoundException.class,
                () -> useCase.execute(randomId));
    }
}

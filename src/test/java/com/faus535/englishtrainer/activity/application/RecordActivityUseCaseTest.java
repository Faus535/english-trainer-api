package com.faus535.englishtrainer.activity.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.infrastructure.InMemoryActivityDateRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

final class RecordActivityUseCaseTest {

    private InMemoryActivityDateRepository repository;
    private RecordActivityUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryActivityDateRepository();
        ApplicationEventPublisher publisher = event -> {};
        useCase = new RecordActivityUseCase(repository, publisher);
    }

    @Test
    void shouldRecordActivityDate() {
        UserProfileId userId = UserProfileId.generate();
        LocalDate today = LocalDate.now();

        ActivityDate result = useCase.execute(userId, today);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(today, result.activityDate());
    }

    @Test
    void shouldReturnExistingWhenAlreadyRecorded() {
        UserProfileId userId = UserProfileId.generate();
        LocalDate today = LocalDate.now();

        ActivityDate first = useCase.execute(userId, today);
        ActivityDate second = useCase.execute(userId, today);

        assertEquals(first.id(), second.id());
    }
}

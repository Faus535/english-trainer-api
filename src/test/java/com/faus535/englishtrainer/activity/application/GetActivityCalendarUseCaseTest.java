package com.faus535.englishtrainer.activity.application;

import com.faus535.englishtrainer.activity.domain.ActivityDateMother;
import com.faus535.englishtrainer.activity.infrastructure.InMemoryActivityDateRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetActivityCalendarUseCaseTest {

    private InMemoryActivityDateRepository repository;
    private GetActivityCalendarUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryActivityDateRepository();
        useCase = new GetActivityCalendarUseCase(repository);
    }

    @Test
    void shouldReturnDatesForSpecificMonth() {
        UserProfileId userId = UserProfileId.generate();
        LocalDate marchDate = LocalDate.of(2026, 3, 15);
        LocalDate aprilDate = LocalDate.of(2026, 4, 1);
        repository.save(ActivityDateMother.create(userId, marchDate));
        repository.save(ActivityDateMother.create(userId, aprilDate));

        List<LocalDate> result = useCase.execute(userId, 2026, 3);

        assertEquals(1, result.size());
        assertEquals(marchDate, result.get(0));
    }

    @Test
    void shouldReturnEmptyForMonthWithNoActivity() {
        UserProfileId userId = UserProfileId.generate();
        repository.save(ActivityDateMother.create(userId, LocalDate.of(2026, 3, 15)));

        List<LocalDate> result = useCase.execute(userId, 2026, 6);

        assertTrue(result.isEmpty());
    }
}

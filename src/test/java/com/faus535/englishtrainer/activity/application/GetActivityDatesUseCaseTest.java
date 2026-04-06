package com.faus535.englishtrainer.activity.application;

import com.faus535.englishtrainer.activity.domain.ActivityDateMother;
import com.faus535.englishtrainer.activity.infrastructure.InMemoryActivityDateRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetActivityDatesUseCaseTest {

    private InMemoryActivityDateRepository repository;
    private GetActivityDatesUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryActivityDateRepository();
        useCase = new GetActivityDatesUseCase(repository);
    }

    @Test
    void shouldReturnSortedDates() {
        UserProfileId userId = UserProfileId.generate();
        LocalDate date1 = LocalDate.of(2026, 3, 15);
        LocalDate date2 = LocalDate.of(2026, 3, 10);
        LocalDate date3 = LocalDate.of(2026, 3, 20);
        repository.save(ActivityDateMother.create(userId, date1));
        repository.save(ActivityDateMother.create(userId, date2));
        repository.save(ActivityDateMother.create(userId, date3));

        List<LocalDate> result = useCase.execute(userId);

        assertEquals(3, result.size());
        assertEquals(date2, result.get(0));
        assertEquals(date1, result.get(1));
        assertEquals(date3, result.get(2));
    }

    @Test
    void shouldReturnEmptyForUnknownUser() {
        UserProfileId unknownUser = UserProfileId.generate();

        List<LocalDate> result = useCase.execute(unknownUser);

        assertTrue(result.isEmpty());
    }
}

package com.faus535.englishtrainer.learningpath.infrastructure.controller;

import com.faus535.englishtrainer.learningpath.application.GetLearningStatusUseCase;
import com.faus535.englishtrainer.learningpath.application.GetLearningStatusUseCase.LearningStatus;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathNotFoundException;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetLearningStatusControllerTest {

    private final GetLearningStatusUseCase useCase = mock(GetLearningStatusUseCase.class);
    private final GetLearningStatusController controller = new GetLearningStatusController(useCase);

    @Test
    void shouldReturn200WithStatusWhenLearningPathExists() throws Exception {
        UUID profileId = UUID.randomUUID();
        LearningStatus status = new LearningStatus(
                new GetLearningStatusUseCase.UnitSummary("Greetings", 0, 50, "IN_PROGRESS",
                        new GetLearningStatusUseCase.ContentProgress(1, 3)),
                new GetLearningStatusUseCase.NextUnit("Daily Routines", 1),
                new GetLearningStatusUseCase.OverallProgress(0, 2, 0),
                new GetLearningStatusUseCase.TodaysPlan(2, 0, 1, "learn"),
                List.of(),
                3,
                List.of()
        );

        when(useCase.execute(any(UserProfileId.class))).thenReturn(status);

        ResponseEntity<GetLearningStatusController.LearningStatusResponse> response = controller.handle(profileId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Greetings", response.getBody().currentUnit().name());
        assertEquals(50, response.getBody().currentUnit().masteryScore());
    }

    @Test
    void shouldReturn204WhenNoLearningPath() throws Exception {
        UUID profileId = UUID.randomUUID();

        when(useCase.execute(any(UserProfileId.class)))
                .thenThrow(new LearningPathNotFoundException(new UserProfileId(profileId)));

        ResponseEntity<GetLearningStatusController.LearningStatusResponse> response = controller.handle(profileId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}

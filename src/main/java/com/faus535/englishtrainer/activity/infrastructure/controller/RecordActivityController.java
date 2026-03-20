package com.faus535.englishtrainer.activity.infrastructure.controller;

import com.faus535.englishtrainer.activity.application.RecordActivityUseCase;
import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
class RecordActivityController {

    private final RecordActivityUseCase useCase;

    RecordActivityController(RecordActivityUseCase useCase) {
        this.useCase = useCase;
    }

    record RecordActivityRequest(String date) {}

    record ActivityResponse(String id, String userId, String activityDate) {}

    @PostMapping("/api/profiles/{userId}/activity")
    ResponseEntity<ActivityResponse> handle(@PathVariable String userId,
                                            @RequestBody(required = false) RecordActivityRequest request) {
        UserProfileId userProfileId = UserProfileId.fromString(userId);
        LocalDate date = (request != null && request.date() != null && !request.date().isBlank())
                ? LocalDate.parse(request.date())
                : LocalDate.now();

        ActivityDate activityDate = useCase.execute(userProfileId, date);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(activityDate));
    }

    private ActivityResponse toResponse(ActivityDate activityDate) {
        return new ActivityResponse(
                activityDate.id().value().toString(),
                activityDate.userId().value().toString(),
                activityDate.activityDate().toString()
        );
    }
}

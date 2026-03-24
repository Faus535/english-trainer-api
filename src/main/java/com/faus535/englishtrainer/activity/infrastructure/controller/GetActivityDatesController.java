package com.faus535.englishtrainer.activity.infrastructure.controller;

import com.faus535.englishtrainer.activity.application.GetActivityCalendarUseCase;
import com.faus535.englishtrainer.activity.application.GetActivityDatesUseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.ProfileOwnershipException;
import com.faus535.englishtrainer.user.infrastructure.controller.ProfileOwnershipChecker;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
class GetActivityDatesController {

    private final GetActivityDatesUseCase getActivityDatesUseCase;
    private final GetActivityCalendarUseCase getActivityCalendarUseCase;

    GetActivityDatesController(GetActivityDatesUseCase getActivityDatesUseCase,
                               GetActivityCalendarUseCase getActivityCalendarUseCase) {
        this.getActivityDatesUseCase = getActivityDatesUseCase;
        this.getActivityCalendarUseCase = getActivityCalendarUseCase;
    }

    @GetMapping("/api/profiles/{userId}/activity")
    ResponseEntity<List<String>> handle(@PathVariable String userId,
                                        @RequestParam(required = false) Integer year,
                                        @RequestParam(required = false) Integer month,
                                        Authentication authentication)
            throws ProfileOwnershipException {
        ProfileOwnershipChecker.check(authentication, UUID.fromString(userId));
        UserProfileId userProfileId = UserProfileId.fromString(userId);

        List<LocalDate> dates;
        if (year != null && month != null) {
            dates = getActivityCalendarUseCase.execute(userProfileId, year, month);
        } else {
            dates = getActivityDatesUseCase.execute(userProfileId);
        }

        List<String> dateStrings = dates.stream()
                .map(LocalDate::toString)
                .toList();

        return ResponseEntity.ok(dateStrings);
    }
}

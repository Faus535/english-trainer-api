package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.application.GetTodayPhonemeUseCase;
import com.faus535.englishtrainer.phonetics.application.GetTodayPhonemeUseCase.TodayPhonemeResult;
import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import com.faus535.englishtrainer.phonetics.domain.error.NoPhonemesAvailableException;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetTodayPhonemeController {

    private final GetTodayPhonemeUseCase useCase;

    GetTodayPhonemeController(GetTodayPhonemeUseCase useCase) {
        this.useCase = useCase;
    }

    record PhonemeDto(String id, String symbol, String name, String category,
                      String subcategory, List<String> exampleWords,
                      String description, String mouthPosition, String tips) {}

    record ProgressDto(int attemptsCount, int correctAttemptsCount, boolean completed,
                       int phrasesCompleted, int phrasesTotal) {}

    record TodayPhonemeResponse(PhonemeDto phoneme, String assignedDate, ProgressDto progress,
                                int completedCount, int totalCount) {}

    @GetMapping("/api/profiles/{userId}/phonetics/today")
    @RequireProfileOwnership
    ResponseEntity<TodayPhonemeResponse> handle(@PathVariable UUID userId,
                                                  Authentication authentication)
            throws NoPhonemesAvailableException {
        TodayPhonemeResult result = useCase.execute(new UserProfileId(userId));
        return ResponseEntity.ok(toResponse(result));
    }

    private TodayPhonemeResponse toResponse(TodayPhonemeResult result) {
        Phoneme p = result.phoneme();
        var progress = result.progress();
        return new TodayPhonemeResponse(
                new PhonemeDto(p.id().value().toString(), p.symbol(), p.name(),
                        p.category().value(), p.subcategory(), p.exampleWords(),
                        p.description(), p.mouthPosition(), p.tips()),
                result.assignedDate().toString(),
                new ProgressDto(progress.attemptsCount(), progress.correctAttemptsCount(),
                        progress.completed(), progress.phrasesCompleted(), progress.phrasesTotal()),
                result.completedCount(), result.totalCount()
        );
    }
}

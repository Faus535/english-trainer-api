package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.application.GetUserPhonemeProgressUseCase;
import com.faus535.englishtrainer.phonetics.application.GetUserPhonemeProgressUseCase.PhonemeProgressItem;
import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
class GetUserPhonemeProgressController {

    private final GetUserPhonemeProgressUseCase useCase;

    GetUserPhonemeProgressController(GetUserPhonemeProgressUseCase useCase) {
        this.useCase = useCase;
    }

    record PhonemeProgressDto(String phonemeId, String symbol, String name,
                               String category, int difficultyOrder,
                               boolean completed, String completedAt) {}

    @GetMapping("/api/profiles/{userId}/phonetics/progress")
    @RequireProfileOwnership
    ResponseEntity<List<PhonemeProgressDto>> handle(@PathVariable UUID userId,
                                                     Authentication authentication) {
        List<PhonemeProgressDto> response = useCase.execute(new UserProfileId(userId)).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    private PhonemeProgressDto toDto(PhonemeProgressItem item) {
        Phoneme p = item.phoneme();
        return new PhonemeProgressDto(
            p.id().value().toString(), p.symbol(), p.name(),
            p.category().value(), p.difficultyOrder(),
            item.completed(), item.completedAt() != null ? item.completedAt().toString() : null
        );
    }
}

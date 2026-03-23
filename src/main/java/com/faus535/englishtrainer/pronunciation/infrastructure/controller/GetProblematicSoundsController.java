package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.application.GetProblematicSoundsUseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetProblematicSoundsController {

    private final GetProblematicSoundsUseCase useCase;

    GetProblematicSoundsController(GetProblematicSoundsUseCase useCase) {
        this.useCase = useCase;
    }

    record ProblematicSoundResponse(String phoneme, int errorCount, List<String> topWords, String lastOccurred) {}

    @GetMapping("/api/profiles/{userId}/pronunciation/problematic-sounds")
    ResponseEntity<List<ProblematicSoundResponse>> handle(@PathVariable String userId) {
        List<ProblematicSoundResponse> response = useCase.execute(UserProfileId.fromString(userId)).stream()
                .map(s -> new ProblematicSoundResponse(s.phoneme(), s.errorCount(), s.topWords(), s.lastOccurred()))
                .toList();
        return ResponseEntity.ok(response);
    }
}

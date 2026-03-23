package com.faus535.englishtrainer.minigame.infrastructure.controller;

import com.faus535.englishtrainer.minigame.domain.MiniGameScore;
import com.faus535.englishtrainer.minigame.domain.MiniGameScoreRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
class GetMiniGameScoresController {

    private final MiniGameScoreRepository scoreRepository;

    GetMiniGameScoresController(MiniGameScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    record MiniGameScoreResponse(UUID id, String gameType, int score, int xpEarned, LocalDateTime playedAt) {}

    @GetMapping("/api/profiles/{userId}/minigames/scores")
    ResponseEntity<List<MiniGameScoreResponse>> handle(@PathVariable UUID userId,
                                                        @RequestParam String type) {
        UserProfileId profileId = new UserProfileId(userId);
        List<MiniGameScore> scores = scoreRepository.findByUserIdAndGameType(profileId, type);
        List<MiniGameScoreResponse> response = scores.stream()
                .map(score -> new MiniGameScoreResponse(
                        score.id().value(),
                        score.gameType(),
                        score.score(),
                        score.xpEarned(),
                        score.playedAt()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
}

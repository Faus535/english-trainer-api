package com.faus535.englishtrainer.minigame.application;

import com.faus535.englishtrainer.minigame.domain.MiniGameScore;
import com.faus535.englishtrainer.minigame.domain.MiniGameScoreRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class SaveMiniGameScoreUseCase {

    private final MiniGameScoreRepository scoreRepository;

    public SaveMiniGameScoreUseCase(MiniGameScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public record SaveScoreResult(int xpEarned, int score, String gameType) {}

    @Transactional
    public SaveScoreResult execute(UserProfileId userId, String gameType, int score) {
        int xpEarned = calculateXp(score);
        MiniGameScore miniGameScore = MiniGameScore.create(userId, gameType, score, xpEarned);
        scoreRepository.save(miniGameScore);
        return new SaveScoreResult(xpEarned, score, gameType);
    }

    private int calculateXp(int score) {
        int xp = (score / 10) * 5;
        return Math.max(5, Math.min(25, xp));
    }
}

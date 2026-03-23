package com.faus535.englishtrainer.dailychallenge.application;

import com.faus535.englishtrainer.dailychallenge.domain.DailyChallenge;
import com.faus535.englishtrainer.dailychallenge.domain.DailyChallengeRepository;
import com.faus535.englishtrainer.dailychallenge.domain.UserChallenge;
import com.faus535.englishtrainer.dailychallenge.domain.UserChallengeId;
import com.faus535.englishtrainer.dailychallenge.domain.UserChallengeRepository;
import com.faus535.englishtrainer.dailychallenge.domain.error.ChallengeNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@UseCase
public class UpdateChallengeProgressUseCase {

    private final DailyChallengeRepository dailyChallengeRepository;
    private final UserChallengeRepository userChallengeRepository;

    public UpdateChallengeProgressUseCase(DailyChallengeRepository dailyChallengeRepository,
                                           UserChallengeRepository userChallengeRepository) {
        this.dailyChallengeRepository = dailyChallengeRepository;
        this.userChallengeRepository = userChallengeRepository;
    }

    public record ProgressResult(int progress, int target, boolean completed, int xpReward) {}

    @Transactional
    public ProgressResult execute(UserProfileId userId, int progress) throws ChallengeNotFoundException {
        LocalDate today = LocalDate.now();
        DailyChallenge challenge = dailyChallengeRepository.findByDate(today)
                .orElseThrow(() -> new ChallengeNotFoundException(today));

        UserChallenge userChallenge = userChallengeRepository
                .findByUserIdAndChallengeId(userId, challenge.id())
                .orElseGet(() -> UserChallenge.create(UserChallengeId.generate(), userId, challenge.id()));

        UserChallenge updated = userChallenge.updateProgress(progress);

        if (updated.progress() >= challenge.target() && !updated.completed()) {
            updated = updated.complete();
        }

        userChallengeRepository.save(updated);

        return new ProgressResult(
                updated.progress(),
                challenge.target(),
                updated.completed(),
                updated.completed() ? challenge.xpReward() : 0
        );
    }
}

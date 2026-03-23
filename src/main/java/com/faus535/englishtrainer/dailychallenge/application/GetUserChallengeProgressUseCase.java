package com.faus535.englishtrainer.dailychallenge.application;

import com.faus535.englishtrainer.dailychallenge.domain.DailyChallenge;
import com.faus535.englishtrainer.dailychallenge.domain.DailyChallengeRepository;
import com.faus535.englishtrainer.dailychallenge.domain.UserChallenge;
import com.faus535.englishtrainer.dailychallenge.domain.UserChallengeRepository;
import com.faus535.englishtrainer.dailychallenge.domain.error.ChallengeNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@UseCase
public class GetUserChallengeProgressUseCase {

    private final DailyChallengeRepository dailyChallengeRepository;
    private final UserChallengeRepository userChallengeRepository;

    public GetUserChallengeProgressUseCase(DailyChallengeRepository dailyChallengeRepository,
                                            UserChallengeRepository userChallengeRepository) {
        this.dailyChallengeRepository = dailyChallengeRepository;
        this.userChallengeRepository = userChallengeRepository;
    }

    public record UserChallengeProgress(DailyChallenge challenge, int progress, boolean completed) {}

    @Transactional(readOnly = true)
    public UserChallengeProgress execute(UserProfileId userId) throws ChallengeNotFoundException {
        LocalDate today = LocalDate.now();
        DailyChallenge challenge = dailyChallengeRepository.findByDate(today)
                .orElseThrow(() -> new ChallengeNotFoundException(today));

        Optional<UserChallenge> userChallenge = userChallengeRepository
                .findByUserIdAndChallengeId(userId, challenge.id());

        int progress = userChallenge.map(UserChallenge::progress).orElse(0);
        boolean completed = userChallenge.map(UserChallenge::completed).orElse(false);

        return new UserChallengeProgress(challenge, progress, completed);
    }
}

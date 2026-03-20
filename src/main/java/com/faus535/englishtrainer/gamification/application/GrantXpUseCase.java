package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

@UseCase
public final class GrantXpUseCase {

    private final UserProfileRepository userProfileRepository;

    public GrantXpUseCase(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public record XpGrantResult(int xpGranted, int totalXp) {}

    public XpGrantResult execute(UserProfileId userId, int baseXp, String reason) throws UserProfileNotFoundException, InvalidXpAmountException {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(userId));

        UserProfile updated = profile.addXp(baseXp);
        userProfileRepository.save(updated);

        return new XpGrantResult(baseXp, updated.xp());
    }
}

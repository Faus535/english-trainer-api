package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.gamification.domain.XpLevel;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class GetXpLevelUseCase {

    private final UserProfileRepository userProfileRepository;

    public GetXpLevelUseCase(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public XpLevel execute(UserProfileId id) throws UserProfileNotFoundException {
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException(id));
        return XpLevel.fromXp(profile.xp());
    }
}

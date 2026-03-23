package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.ModuleWeight;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionGenerator;
import com.faus535.englishtrainer.session.domain.SessionMode;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.session.domain.error.ActiveSessionExistsException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GenerateSessionUseCase {

    private final UserProfileRepository userProfileRepository;
    private final SessionRepository sessionRepository;

    public GenerateSessionUseCase(UserProfileRepository userProfileRepository,
                                  SessionRepository sessionRepository) {
        this.userProfileRepository = userProfileRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public Session execute(UserProfileId userId, SessionMode mode) throws UserProfileNotFoundException, ActiveSessionExistsException {
        return execute(userId, mode, null);
    }

    @Transactional
    public Session execute(UserProfileId userId, SessionMode mode, List<ModuleWeight> weights)
            throws UserProfileNotFoundException, ActiveSessionExistsException {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(userId));

        if (sessionRepository.findActiveByUser(userId).isPresent()) {
            throw new ActiveSessionExistsException();
        }

        int sessionCount = profile.sessionCount();
        Session session;

        if (SessionGenerator.shouldBeIntegrator(sessionCount)) {
            String theme = "integration-review-" + sessionCount;
            session = SessionGenerator.generateIntegrator(userId, mode, theme);
        } else {
            session = SessionGenerator.generateNormal(userId, mode, sessionCount, weights);
        }

        return sessionRepository.save(session);
    }
}

package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

@UseCase
public final class CompleteSessionUseCase {

    private final SessionRepository sessionRepository;
    private final UserProfileRepository userProfileRepository;

    public CompleteSessionUseCase(SessionRepository sessionRepository,
                                 UserProfileRepository userProfileRepository) {
        this.sessionRepository = sessionRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public Session execute(SessionId sessionId, int durationMinutes)
            throws SessionNotFoundException, UserProfileNotFoundException {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        Session completedSession = session.complete(durationMinutes);

        UserProfile profile = userProfileRepository.findById(session.userId())
                .orElseThrow(() -> new UserProfileNotFoundException(session.userId()));

        UserProfile updatedProfile = profile.recordSession();

        userProfileRepository.save(updatedProfile);
        return sessionRepository.save(completedSession);
    }
}

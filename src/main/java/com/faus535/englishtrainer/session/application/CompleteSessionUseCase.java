package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class CompleteSessionUseCase {

    private final SessionRepository sessionRepository;
    private final UserProfileRepository userProfileRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CompleteSessionUseCase(SessionRepository sessionRepository,
                                 UserProfileRepository userProfileRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.userProfileRepository = userProfileRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Session execute(SessionId sessionId, int durationMinutes)
            throws SessionNotFoundException, UserProfileNotFoundException {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        Session completedSession = session.complete(durationMinutes);

        UserProfile profile = userProfileRepository.findById(session.userId())
                .orElseThrow(() -> new UserProfileNotFoundException(session.userId()));

        UserProfile updatedProfile = profile.recordSession();

        userProfileRepository.save(updatedProfile);
        Session saved = sessionRepository.save(completedSession);
        completedSession.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved;
    }
}

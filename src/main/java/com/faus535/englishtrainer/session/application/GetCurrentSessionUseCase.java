package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.Optional;

@UseCase
public final class GetCurrentSessionUseCase {

    private final SessionRepository sessionRepository;

    public GetCurrentSessionUseCase(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Optional<Session> execute(UserProfileId userId) {
        return sessionRepository.findActiveByUser(userId);
    }
}

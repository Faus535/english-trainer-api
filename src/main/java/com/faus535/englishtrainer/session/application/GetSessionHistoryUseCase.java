package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

@UseCase
public final class GetSessionHistoryUseCase {

    private final SessionRepository sessionRepository;

    public GetSessionHistoryUseCase(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public List<Session> execute(UserProfileId userId) {
        return sessionRepository.findByUser(userId);
    }
}

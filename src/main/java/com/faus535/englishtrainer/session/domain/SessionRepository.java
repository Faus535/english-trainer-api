package com.faus535.englishtrainer.session.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {

    Optional<Session> findById(SessionId id);

    Optional<Session> findActiveByUser(UserProfileId userId);

    List<Session> findByUser(UserProfileId userId);

    Session save(Session session);
}

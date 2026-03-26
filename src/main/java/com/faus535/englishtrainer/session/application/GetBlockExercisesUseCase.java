package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionExercise;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

@UseCase
public class GetBlockExercisesUseCase {

    private final SessionRepository sessionRepository;

    public GetBlockExercisesUseCase(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public List<SessionExercise> execute(UserProfileId userId, SessionId sessionId, int blockIndex)
            throws SessionNotFoundException {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        if (!session.userId().equals(userId)) {
            throw new SessionNotFoundException(sessionId);
        }

        return session.getExercisesForBlock(blockIndex);
    }
}

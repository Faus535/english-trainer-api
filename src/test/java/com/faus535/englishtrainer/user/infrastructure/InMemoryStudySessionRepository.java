package com.faus535.englishtrainer.user.infrastructure;

import com.faus535.englishtrainer.user.domain.StudySession;
import com.faus535.englishtrainer.user.domain.StudySessionRepository;
import com.faus535.englishtrainer.user.domain.vo.StudySessionId;

import java.util.HashMap;
import java.util.Map;

public final class InMemoryStudySessionRepository implements StudySessionRepository {

    private final Map<StudySessionId, StudySession> store = new HashMap<>();

    @Override
    public void save(StudySession session) {
        store.put(session.id(), session);
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}

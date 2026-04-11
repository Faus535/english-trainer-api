package com.faus535.englishtrainer.user.domain;

import com.faus535.englishtrainer.user.domain.vo.StudyModule;

import java.util.UUID;

public final class StudySessionMother {

    public static StudySession create() {
        return StudySession.create(UUID.randomUUID(), StudyModule.ARTICLE, 300);
    }

    public static StudySession forArticle(UUID userId) {
        return StudySession.create(userId, StudyModule.ARTICLE, 600);
    }
}

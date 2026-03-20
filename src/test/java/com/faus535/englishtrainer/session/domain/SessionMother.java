package com.faus535.englishtrainer.session.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

public final class SessionMother {

    private SessionMother() {}

    public static Session create() {
        return Session.create(
                UserProfileId.generate(),
                new SessionMode("full"),
                new SessionType("normal"),
                "listening",
                "vocabulary",
                null,
                List.of(
                        new SessionBlock("warmup", "review", 3),
                        new SessionBlock("listening", "listening", 7),
                        new SessionBlock("secondary", "vocabulary", 7),
                        new SessionBlock("practice", "mixed", 4)
                )
        );
    }

    public static Session create(UserProfileId userId) {
        return Session.create(
                userId,
                new SessionMode("full"),
                new SessionType("normal"),
                "listening",
                "vocabulary",
                null,
                List.of(
                        new SessionBlock("warmup", "review", 3),
                        new SessionBlock("listening", "listening", 7),
                        new SessionBlock("secondary", "vocabulary", 7),
                        new SessionBlock("practice", "mixed", 4)
                )
        );
    }
}

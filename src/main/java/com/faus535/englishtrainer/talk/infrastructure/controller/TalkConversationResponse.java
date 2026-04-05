package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.domain.*;

import java.time.Instant;
import java.util.List;

record TalkConversationResponse(
        String id,
        String userId,
        String scenarioId,
        String level,
        String status,
        Instant startedAt,
        Instant endedAt,
        List<TalkMessageResponse> messages
) {

    record TalkMessageResponse(
            String id,
            String role,
            String content,
            TalkCorrection correction,
            Instant createdAt
    ) {
        static TalkMessageResponse from(TalkMessage message) {
            return new TalkMessageResponse(
                    message.id().value().toString(),
                    message.role(),
                    message.content(),
                    message.correction(),
                    message.createdAt()
            );
        }
    }

    static TalkConversationResponse from(TalkConversation conversation) {
        return new TalkConversationResponse(
                conversation.id().value().toString(),
                conversation.userId().toString(),
                conversation.scenarioId() != null ? conversation.scenarioId().toString() : null,
                conversation.level().value(),
                conversation.status().value(),
                conversation.startedAt(),
                conversation.endedAt(),
                conversation.messages().stream().map(TalkMessageResponse::from).toList()
        );
    }
}

package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.domain.Conversation;
import com.faus535.englishtrainer.conversation.domain.ConversationTurn;
import com.faus535.englishtrainer.conversation.domain.TutorFeedback;

import java.time.Instant;
import java.util.List;

record ConversationResponse(
        String id,
        String userId,
        String level,
        String topic,
        String status,
        String summary,
        Instant startedAt,
        Instant endedAt,
        List<TurnResponse> turns
) {

    record TurnResponse(
            String id,
            String role,
            String content,
            TutorFeedback feedback,
            Float confidence,
            Instant createdAt
    ) {
        static TurnResponse from(ConversationTurn turn) {
            return new TurnResponse(
                    turn.id().value().toString(),
                    turn.role(),
                    turn.content(),
                    turn.feedback(),
                    turn.confidence(),
                    turn.createdAt()
            );
        }
    }

    static ConversationResponse from(Conversation conversation) {
        return new ConversationResponse(
                conversation.id().value().toString(),
                conversation.userId().toString(),
                conversation.level().value(),
                conversation.topic(),
                conversation.status().value(),
                conversation.summary(),
                conversation.startedAt(),
                conversation.endedAt(),
                conversation.turns().stream().map(TurnResponse::from).toList()
        );
    }
}

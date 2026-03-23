package com.faus535.englishtrainer.conversation.domain;

import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AiTutorStreamPort {

    Flux<StreamEvent> chatStream(ConversationLevel level, String topic, List<ConversationTurn> turns,
                                  Float confidence) throws AiTutorException;

    sealed interface StreamEvent permits StreamEvent.TextChunk, StreamEvent.Feedback {
        record TextChunk(String text) implements StreamEvent {}
        record Feedback(TutorFeedback feedback, String fullContent) implements StreamEvent {}
    }
}

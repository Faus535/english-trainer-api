package com.faus535.englishtrainer.conversation.domain;

import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AiTutorStreamPort {

    Flux<String> chatStream(ConversationLevel level, String topic, List<ConversationTurn> turns,
                            Float confidence) throws AiTutorException;
}

package com.faus535.englishtrainer.talk.domain.error;

import com.faus535.englishtrainer.talk.domain.TalkConversationId;

public final class TalkConversationNotFoundException extends TalkException {

    public TalkConversationNotFoundException(TalkConversationId id) {
        super("Talk conversation not found: " + id.value());
    }
}

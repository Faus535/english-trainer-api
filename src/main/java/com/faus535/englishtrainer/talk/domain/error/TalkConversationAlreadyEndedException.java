package com.faus535.englishtrainer.talk.domain.error;

import com.faus535.englishtrainer.talk.domain.TalkConversationId;

public final class TalkConversationAlreadyEndedException extends TalkException {

    public TalkConversationAlreadyEndedException(TalkConversationId id) {
        super("Talk conversation already ended: " + id.value());
    }
}

package com.faus535.englishtrainer.talk.domain.error;

public final class TalkMaxConversationsExceededException extends TalkException {

    public TalkMaxConversationsExceededException(int max) {
        super("Maximum active conversations exceeded: " + max);
    }
}

package com.faus535.englishtrainer.conversation.domain;

import java.util.List;

public record ConversationGoal(
        String type,
        String description,
        List<String> targetItems
) {

    public ConversationGoal {
        targetItems = targetItems != null ? List.copyOf(targetItems) : List.of();
    }
}

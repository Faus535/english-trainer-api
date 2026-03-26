package com.faus535.englishtrainer.session.domain;

import java.util.List;
import java.util.UUID;

public record SessionBlock(
        String blockType,
        String moduleName,
        int durationMinutes,
        int exerciseCount,
        List<UUID> contentIds
) {

    public SessionBlock(String blockType, String moduleName, int durationMinutes) {
        this(blockType, moduleName, durationMinutes, 0, List.of());
    }

    public SessionBlock {
        contentIds = contentIds != null ? List.copyOf(contentIds) : List.of();
    }
}

package com.faus535.englishtrainer.spacedrepetition.infrastructure.controller;

import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;

record SpacedRepetitionItemResponse(String id, String userId, String itemType, String unitReference,
                                    String moduleName, String level, int unitIndex, String nextReviewDate,
                                    int intervalIndex, int reviewCount, boolean graduated) {

    static SpacedRepetitionItemResponse from(SpacedRepetitionItem item) {
        return new SpacedRepetitionItemResponse(
                item.id().value().toString(),
                item.userId().value().toString(),
                item.itemType(),
                item.unitReference(),
                item.moduleName(),
                item.level(),
                item.unitIndex(),
                item.nextReviewDate().toString(),
                item.intervalIndex(),
                item.reviewCount(),
                item.graduated()
        );
    }
}

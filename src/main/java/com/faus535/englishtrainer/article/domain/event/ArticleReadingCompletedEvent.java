package com.faus535.englishtrainer.article.domain.event;

import java.util.UUID;

public record ArticleReadingCompletedEvent(UUID articleReadingId, UUID userId) {
}

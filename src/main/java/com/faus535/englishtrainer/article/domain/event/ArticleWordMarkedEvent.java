package com.faus535.englishtrainer.article.domain.event;

import java.util.UUID;

public record ArticleWordMarkedEvent(
        UUID articleReadingId,
        UUID userId,
        UUID markedWordId,
        String wordOrPhrase,
        String translation,
        String contextSentence
) {}

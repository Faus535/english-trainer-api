package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ContentType;
import com.faus535.englishtrainer.immerse.domain.ImmerseAiPort;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentRepository;
import com.faus535.englishtrainer.immerse.domain.ImmerseExerciseRepository;

import java.util.UUID;

class SpyProcessImmerseContentAsyncService extends ProcessImmerseContentAsyncService {

    private boolean processCalled;
    private UUID lastContentId;

    SpyProcessImmerseContentAsyncService(ImmerseContentRepository contentRepository,
                                          ImmerseExerciseRepository exerciseRepository,
                                          ImmerseAiPort aiPort) {
        super(contentRepository, exerciseRepository, aiPort, null);
    }

    @Override
    public void process(UUID contentId, ContentType contentType, String level, String topic) {
        this.processCalled = true;
        this.lastContentId = contentId;
    }

    boolean wasProcessCalled() { return processCalled; }

    UUID lastContentId() { return lastContentId; }
}

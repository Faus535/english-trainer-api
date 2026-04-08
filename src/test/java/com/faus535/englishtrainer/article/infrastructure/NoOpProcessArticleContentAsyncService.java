package com.faus535.englishtrainer.article.infrastructure;

import com.faus535.englishtrainer.article.application.ProcessArticleContentAsyncService;

import java.util.UUID;

public class NoOpProcessArticleContentAsyncService extends ProcessArticleContentAsyncService {

    private boolean processCalled = false;

    public NoOpProcessArticleContentAsyncService() {
        super(null, null, null, null);
    }

    @Override
    public void process(UUID articleId, String topic, String level) {
        processCalled = true;
    }

    public boolean wasProcessCalled() {
        return processCalled;
    }
}

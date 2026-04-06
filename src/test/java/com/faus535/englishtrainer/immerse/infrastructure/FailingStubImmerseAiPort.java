package com.faus535.englishtrainer.immerse.infrastructure;

import com.faus535.englishtrainer.immerse.domain.ContentType;
import com.faus535.englishtrainer.immerse.domain.ImmerseAiPort;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseAiException;

public class FailingStubImmerseAiPort implements ImmerseAiPort {

    @Override
    public ImmerseProcessResult processContent(String rawText, String level) throws ImmerseAiException {
        throw new ImmerseAiException("AI unavailable");
    }

    @Override
    public ImmerseGenerateResult generateContent(ContentType contentType, String level, String topic)
            throws ImmerseAiException {
        throw new ImmerseAiException("AI unavailable");
    }
}

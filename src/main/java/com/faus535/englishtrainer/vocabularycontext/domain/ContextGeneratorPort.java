package com.faus535.englishtrainer.vocabularycontext.domain;

public interface ContextGeneratorPort {

    String generateSentences(String word, String level) throws Exception;
}

package com.faus535.englishtrainer.writing.domain;

public interface WritingEvaluatorPort {

    WritingFeedback evaluate(String text, String exercisePrompt, String level) throws Exception;
}

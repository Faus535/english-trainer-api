package com.faus535.englishtrainer.pronunciation.application;

import java.util.List;

public record PronunciationFeedbackDto(int score, List<WordFeedbackDto> wordFeedback, String overallTip) {}

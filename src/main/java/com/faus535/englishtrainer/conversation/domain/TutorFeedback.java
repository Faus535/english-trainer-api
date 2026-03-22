package com.faus535.englishtrainer.conversation.domain;

import java.util.List;

public record TutorFeedback(
        List<String> grammarCorrections,
        List<String> vocabularySuggestions,
        List<String> pronunciationTips,
        String encouragement
) {

    public TutorFeedback {
        grammarCorrections = grammarCorrections != null ? List.copyOf(grammarCorrections) : List.of();
        vocabularySuggestions = vocabularySuggestions != null ? List.copyOf(vocabularySuggestions) : List.of();
        pronunciationTips = pronunciationTips != null ? List.copyOf(pronunciationTips) : List.of();
    }

    public static TutorFeedback empty() {
        return new TutorFeedback(List.of(), List.of(), List.of(), null);
    }
}

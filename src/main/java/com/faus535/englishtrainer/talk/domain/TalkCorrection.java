package com.faus535.englishtrainer.talk.domain;

import java.util.List;

public record TalkCorrection(
        List<String> grammarFixes,
        List<String> vocabularySuggestions,
        List<String> pronunciationTips,
        String encouragement,
        String originalUserMessage
) {

    public TalkCorrection {
        grammarFixes = grammarFixes != null ? List.copyOf(grammarFixes) : List.of();
        vocabularySuggestions = vocabularySuggestions != null ? List.copyOf(vocabularySuggestions) : List.of();
        pronunciationTips = pronunciationTips != null ? List.copyOf(pronunciationTips) : List.of();
    }

    public static TalkCorrection empty() {
        return new TalkCorrection(List.of(), List.of(), List.of(), null, null);
    }

    public boolean hasCorrections() {
        return !grammarFixes.isEmpty() || !vocabularySuggestions.isEmpty() || !pronunciationTips.isEmpty();
    }
}

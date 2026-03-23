package com.faus535.englishtrainer.errorpattern.infrastructure.event;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.conversation.application.VocabularyFeedbackEvent;
import com.faus535.englishtrainer.errorpattern.application.RecordErrorPatternUseCase;
import com.faus535.englishtrainer.errorpattern.domain.ErrorCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class GrammarErrorEventListener {

    private static final Logger log = LoggerFactory.getLogger(GrammarErrorEventListener.class);

    private final AuthUserRepository authUserRepository;
    private final RecordErrorPatternUseCase recordErrorPatternUseCase;

    GrammarErrorEventListener(AuthUserRepository authUserRepository,
                               RecordErrorPatternUseCase recordErrorPatternUseCase) {
        this.authUserRepository = authUserRepository;
        this.recordErrorPatternUseCase = recordErrorPatternUseCase;
    }

    @EventListener
    void handleGrammarFeedback(GrammarFeedbackEvent event) {
        try {
            for (String correction : event.grammarCorrections()) {
                ErrorCategory category = classifyError(correction);
                String pattern = extractPattern(correction);
                recordErrorPatternUseCase.execute(event.userId(), category, pattern, correction);
            }
        } catch (Exception e) {
            log.error("Error recording error patterns: {}", e.getMessage(), e);
        }
    }

    private ErrorCategory classifyError(String correction) {
        String lower = correction.toLowerCase();
        if (lower.contains("tense") || lower.contains("past") || lower.contains("present")
                || lower.contains("future") || lower.contains("continuous")) return ErrorCategory.TENSE;
        if (lower.contains("article") || lower.contains("the ") || lower.contains("a ")
                || lower.contains("an ")) return ErrorCategory.ARTICLE;
        if (lower.contains("preposition") || lower.contains(" in ") || lower.contains(" on ")
                || lower.contains(" at ")) return ErrorCategory.PREPOSITION;
        if (lower.contains("order") || lower.contains("word order")) return ErrorCategory.WORD_ORDER;
        if (lower.contains("agreement") || lower.contains("subject") || lower.contains("verb"))
            return ErrorCategory.SUBJECT_VERB_AGREEMENT;
        if (lower.contains("spelling") || lower.contains("spell")) return ErrorCategory.SPELLING;
        return ErrorCategory.OTHER;
    }

    private String extractPattern(String correction) {
        if (correction.length() > 100) {
            return correction.substring(0, 100);
        }
        return correction;
    }
}

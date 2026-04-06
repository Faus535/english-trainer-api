package com.faus535.englishtrainer.immerse.infrastructure;

import com.faus535.englishtrainer.immerse.domain.ContentType;
import com.faus535.englishtrainer.immerse.domain.ImmerseAiPort;
import com.faus535.englishtrainer.immerse.domain.VocabularyItem;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseAiException;

import java.util.List;

public class StubImmerseAiPort implements ImmerseAiPort {

    @Override
    public ImmerseProcessResult processContent(String rawText, String level) throws ImmerseAiException {
        return new ImmerseProcessResult(
                rawText,
                level != null ? level : "b1",
                List.of(
                        new VocabularyItem("drought", "A long period without rain", "The drought lasted months.", "b2"),
                        new VocabularyItem("emission", "Gas released", "Carbon emissions are rising.", "b2")
                ),
                List.of(
                        new GeneratedExercise("MULTIPLE_CHOICE", "What does 'drought' mean?",
                                "A long period without rain",
                                List.of("A long period without rain", "A storm", "A flood")),
                        new GeneratedExercise("FILL_THE_GAP", "Carbon ___ are rising.",
                                "emissions", List.of())
                )
        );
    }

    @Override
    public ImmerseGenerateResult generateContent(ContentType contentType, String level, String topic)
            throws ImmerseAiException {
        String effectiveLevel = level != null ? level : "b1";
        String text = "The city wakes up early. People rush to work, grab coffee, and check their phones. " +
                "Life in a modern city is fast-paced and full of surprises.";
        return new ImmerseGenerateResult(
                "A Day in the City",
                text,
                text,
                effectiveLevel,
                List.of(
                        new VocabularyItem("rush", "To move quickly", "People rush to work every morning.", effectiveLevel),
                        new VocabularyItem("fast-paced", "Moving or changing quickly", "City life is fast-paced.", effectiveLevel)
                ),
                List.of(
                        new GeneratedExercise("MULTIPLE_CHOICE", "What does 'rush' mean?",
                                "To move quickly",
                                List.of("To move quickly", "To sleep", "To eat")),
                        new GeneratedExercise("TRUE_FALSE", "City life is described as slow and boring.",
                                "false", List.of("true", "false")),
                        new GeneratedExercise("FILL_THE_GAP", "Life in a modern city is ___.",
                                "fast-paced", List.of())
                )
        );
    }
}

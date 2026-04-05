package com.faus535.englishtrainer.immerse.infrastructure;

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
}

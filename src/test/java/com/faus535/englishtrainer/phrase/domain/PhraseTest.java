package com.faus535.englishtrainer.phrase.domain;

import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class PhraseTest {

    @Test
    void shouldCreatePhrase() {
        Phrase phrase = PhraseMother.create();

        assertNotNull(phrase);
        assertNotNull(phrase.id());
    }

    @Test
    void shouldHaveCorrectFields() {
        Phrase phrase = PhraseMother.create();

        assertEquals("How are you?", phrase.en());
        assertEquals("Como estas?", phrase.es());
        assertEquals(new VocabLevel("a1"), phrase.level());
    }

    @Test
    void shouldReconstituteFromExistingData() {
        PhraseId id = PhraseId.generate();
        VocabLevel level = new VocabLevel("b2");

        Phrase phrase = Phrase.reconstitute(id, "Good morning", "Buenos dias", level);

        assertEquals(id, phrase.id());
        assertEquals("Good morning", phrase.en());
        assertEquals("Buenos dias", phrase.es());
        assertEquals(level, phrase.level());
    }
}

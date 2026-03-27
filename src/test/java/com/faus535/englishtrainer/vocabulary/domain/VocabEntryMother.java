package com.faus535.englishtrainer.vocabulary.domain;

public final class VocabEntryMother {

    public static VocabEntry create() {
        return VocabEntry.create(
                VocabEntryId.generate(),
                "hello",
                "/h\u0259\u02c8lo\u028a/",
                "hola",
                "noun",
                "Hello there",
                new VocabLevel("a1")
        );
    }

    public static VocabEntry withLevel(String level) {
        return VocabEntry.create(
                VocabEntryId.generate(),
                "hello",
                "/h\u0259\u02c8lo\u028a/",
                "hola",
                "noun",
                "Hello there",
                new VocabLevel(level)
        );
    }

    public static VocabEntry withEnAndLevel(String en, String es, String level) {
        return VocabEntry.create(
                VocabEntryId.generate(),
                en,
                "/test/",
                es,
                "noun",
                "Example: " + en,
                new VocabLevel(level)
        );
    }
}

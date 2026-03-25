package com.faus535.englishtrainer.writing.infrastructure.ai;

import java.util.Map;

final class WritingRubricProvider {

    private WritingRubricProvider() {}

    private static final Map<String, String> RUBRICS = Map.of(
            "a1", """
                    EVALUATION RUBRIC FOR A1 STUDENT:
                    Grammar (0-100): Only check subject-verb agreement and basic word order. Ignore tense mistakes beyond present simple. Ignore article errors.
                    Vocabulary (0-100): Expect top 300 words. Award points for any correct word usage. Do not penalize repetition.
                    Coherence (0-100): Accept simple list-like sentences. No paragraph structure required. Understandable = good.
                    Scoring guidance: 80+ if message is understandable, even with many errors. Be generous — communication matters more than accuracy at this level.""",

            "a2", """
                    EVALUATION RUBRIC FOR A2 STUDENT:
                    Grammar (0-100): Check present simple, past simple, and 'going to' future. Accept errors in other tenses. Check basic comparatives.
                    Vocabulary (0-100): Expect top 800 frequency words. Award points for correct use of common collocations.
                    Coherence (0-100): Expect related sentences but not necessarily paragraphs. Basic connectors (and, but, because) expected.
                    Scoring guidance: 70+ if student uses past and present correctly most of the time and the text is clearly understandable.""",

            "b1", """
                    EVALUATION RUBRIC FOR B1 STUDENT:
                    Grammar (0-100): Check all basic tenses, first conditional, and relative clauses. Penalize consistent errors but not occasional slips. Check modal verbs.
                    Vocabulary (0-100): Expect 800-1500 frequency band. Award bonus for phrasal verbs and collocations. Penalize overuse of basic words when better alternatives exist.
                    Coherence (0-100): Expect paragraphs with topic sentences. Basic logical connectors (however, because, although) should be present.
                    Scoring guidance: 70+ requires consistent tense usage and clear paragraph structure. Mention paragraph structure in feedback.""",

            "b2", """
                    EVALUATION RUBRIC FOR B2 STUDENT:
                    Grammar (0-100): Check all tenses including passive voice, second/third conditional, and reported speech. Penalize systematic errors. Check subject-verb agreement in complex sentences.
                    Vocabulary (0-100): Expect 1500-2500 frequency band. Award points for idioms, collocations, and varied word choice. Penalize repetitive vocabulary.
                    Coherence (0-100): Expect well-organized paragraphs with clear transitions. Varied connectors (nevertheless, furthermore, on the other hand). Logical argumentation.
                    Scoring guidance: 70+ requires varied sentence structures, appropriate register, and clear argumentation.""",

            "c1", """
                    EVALUATION RUBRIC FOR C1 STUDENT:
                    Grammar (0-100): Check all structures including passive, inversions, subjunctive, mixed conditionals, and participle clauses. Penalize any systematic error. Check for style and register consistency.
                    Vocabulary (0-100): Expect academic word list. Penalize repetitive vocabulary. Award points for sophisticated word choices, precise collocations, and nuanced synonyms. Mention vocabulary sophistication in feedback.
                    Coherence (0-100): Expect well-structured argumentation with introduction, body, conclusion. Varied and sophisticated connectors. Clear thesis and supporting evidence.
                    Scoring guidance: 70+ requires sophisticated vocabulary, varied structures, and clear argumentation. Be strict on style and register.""",

            "c2", """
                    EVALUATION RUBRIC FOR C2 STUDENT:
                    Grammar (0-100): Native-level accuracy expected. Check for subtle errors in pragmatics, ellipsis, and stylistic choices. Penalize any grammatical inaccuracy.
                    Vocabulary (0-100): Expect near-native range. Penalize any imprecise word choice. Award points for low-frequency words used correctly, subtle distinctions, and register awareness.
                    Coherence (0-100): Expect publication-quality structure. Sophisticated rhetorical devices. Nuanced argumentation with counterpoints addressed.
                    Scoring guidance: 70+ requires near-native fluency in all dimensions. Evaluate style, tone, and pragmatic appropriateness."""
    );

    static String getRubric(String level) {
        return RUBRICS.getOrDefault(level.toLowerCase(), RUBRICS.get("b1"));
    }
}

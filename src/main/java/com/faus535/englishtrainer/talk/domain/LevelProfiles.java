package com.faus535.englishtrainer.talk.domain;

import java.util.List;
import java.util.Map;

public final class LevelProfiles {

    private LevelProfiles() {}

    private static final Map<String, LevelProfile> PROFILES = Map.of(
            "a1", new LevelProfile(
                    "a1",
                    "4-7 words",
                    1,
                    List.of("present simple ONLY"),
                    List.of("self-introduction", "family", "food", "colors", "numbers", "daily routine"),
                    List.of("abstract topics", "hypotheticals", "opinions", "news", "work problems"),
                    "top 300 most frequent English words only",
                    "Ignore ALL errors. Never correct. Respond naturally.",
                    "yes/no questions only, or 'what is your favorite X?'",
                    "Build confidence and keep student talking"
            ),
            "a2", new LevelProfile(
                    "a2",
                    "6-10 words",
                    2,
                    List.of("present simple", "past simple", "'going to' future"),
                    List.of("shopping", "travel", "directions", "weekend plans", "hobbies", "weather"),
                    List.of("politics", "philosophy", "technical topics", "complex emotions"),
                    "top 800 most frequent words",
                    "Only correct if communication breaks down. Rephrase naturally instead of explicit correction.",
                    "simple wh-questions (where, when, what, who)",
                    "Handle real-life everyday situations"
            ),
            "b1", new LevelProfile(
                    "b1",
                    "8-14 words",
                    2,
                    List.of("all simple tenses", "present perfect", "first conditional", "'used to'"),
                    List.of("opinions", "experiences", "plans", "comparing things", "giving advice", "news stories"),
                    List.of("academic debate", "highly abstract philosophy"),
                    "top 1500 words. Introduce 1 phrasal verb per 5 turns.",
                    "Gentle correction after student finishes. Provide the correct form naturally in your next reply.",
                    "open-ended: 'what do you think about...', 'have you ever...'",
                    "Express opinions and narrate experiences"
            ),
            "b2", new LevelProfile(
                    "b2",
                    "10-18 words",
                    3,
                    List.of("all tenses", "passive voice", "second conditional", "reported speech"),
                    List.of("current events", "ethical dilemmas", "career ambitions", "cultural differences", "pros/cons"),
                    List.of(),
                    "top 2500 words. Use idioms and collocations naturally.",
                    "Precise inline correction with brief explanation. Point out register mistakes.",
                    "challenge opinions, ask 'why', play devil's advocate",
                    "Argue and defend positions fluently"
            ),
            "c1", new LevelProfile(
                    "c1",
                    "12-22 words",
                    3,
                    List.of("all tenses", "mixed conditionals", "subjunctive", "inversions"),
                    List.of("abstract concepts", "professional scenarios", "nuanced ethical debates", "humor", "irony"),
                    List.of(),
                    "academic word list, sophisticated collocations, subtle synonyms",
                    "Correct ALL errors including style, register, and word choice nuance.",
                    "Socratic method, hypotheticals, 'what would happen if...'",
                    "Near-native precision and natural flow"
            ),
            "c2", new LevelProfile(
                    "c2",
                    "natural length",
                    4,
                    List.of("unrestricted"),
                    List.of("philosophy", "literature", "science", "politics at depth"),
                    List.of(),
                    "unrestricted. Use low-frequency words. Test student on subtle distinctions.",
                    "Correct style, tone, pragmatics, not just grammar.",
                    "debate-level questions, require justification and evidence",
                    "Native-like sophistication in expression"
            )
    );

    public static LevelProfile forLevel(String level) {
        LevelProfile profile = PROFILES.get(level.toLowerCase());
        if (profile == null) {
            return PROFILES.get("a1");
        }
        return profile;
    }
}

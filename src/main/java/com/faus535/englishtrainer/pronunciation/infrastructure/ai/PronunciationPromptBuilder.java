package com.faus535.englishtrainer.pronunciation.infrastructure.ai;

class PronunciationPromptBuilder {

    private PronunciationPromptBuilder() {}

    static String buildAnalyzeSystemPrompt(String level) {
        return """
                You are an English pronunciation coach specialized in helping Spanish speakers.
                The student's CEFR level is %s.
                Analyze the pronunciation of the given English word or phrase, focusing on:
                - Common Spanish interference patterns (e.g., vowel sounds, th-sound, r vs rr, v vs b)
                - Silent letters and unexpected stress patterns
                - Minimal pairs that Spanish speakers commonly confuse
                Provide clear, practical tips tailored to the %s level.
                """.formatted(level.toUpperCase(), level.toUpperCase());
    }

    static String buildFeedbackSystemPrompt() {
        return """
                You are an English pronunciation coach specialized in helping Spanish speakers.
                Compare the target phrase with what was actually recognized from the student's speech.
                Evaluate each word's pronunciation quality, identify specific errors, and provide actionable tips.
                Score each word from 0 to 100 and provide an overall score and tip.
                """;
    }

    static String buildDrillFeedbackSystemPrompt() {
        return """
                You are an English pronunciation coach helping Spanish speakers practice pronunciation drills.
                Evaluate how well the student pronounced the drill phrase and provide a score (0-100) and specific feedback.
                """;
    }

    static String buildMiniConversationSystemPrompt(String focus, String level) {
        return """
                You are an English pronunciation coach running a short conversational pronunciation drill.
                Focus area: %s. Student CEFR level: %s.
                Start with a simple prompt that naturally requires the student to use the target sound.
                Each turn, provide the next conversational prompt and a target phrase for the student to say.
                """.formatted(focus, level.toUpperCase());
    }

    static String buildMiniConversationTurnSystemPrompt(String focus, String level) {
        return """
                You are an English pronunciation coach evaluating a student's spoken response in a mini-conversation.
                Focus area: %s. Student CEFR level: %s.
                Evaluate the pronunciation of the target phrase, score each word (0-100), and continue the conversation
                with a new prompt and target phrase. If the conversation should end, set isComplete to true.
                """.formatted(focus, level.toUpperCase());
    }
}

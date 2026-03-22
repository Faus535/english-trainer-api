package com.faus535.englishtrainer.conversation.infrastructure.ai;

import com.faus535.englishtrainer.conversation.domain.ConversationLevel;

import java.util.Map;

class SystemPromptBuilder {

    private static final Map<String, String> ROLE_PLAY_SCENARIOS = Map.of(
            "job-interview", "You are a job interviewer conducting a professional interview. Ask about experience, skills, and motivation.",
            "restaurant", "You are a waiter at a restaurant. Help the student order food, describe menu items, and handle requests.",
            "doctor-visit", "You are a doctor seeing a patient. Ask about symptoms, give advice, and explain treatments in simple terms.",
            "travel", "You are a tourist information officer. Help the student plan a trip, give directions, and recommend places.",
            "hotel-checkin", "You are a hotel receptionist. Help the student check in, explain amenities, and handle room requests.",
            "shopping", "You are a shop assistant. Help the student find products, compare items, and complete a purchase."
    );

    static String build(ConversationLevel level, String topic, Float confidence) {
        StringBuilder prompt = new StringBuilder();

        String scenario = topic != null ? ROLE_PLAY_SCENARIOS.get(topic.toLowerCase()) : null;
        if (scenario != null) {
            prompt.append("You are playing a role in an English practice scenario. ").append(scenario).append(" ");
            prompt.append("Stay in character while also being an English tutor. ");
            prompt.append("Your student's CEFR level is ").append(level.value().toUpperCase()).append(". ");
        } else {
            prompt.append("You are a friendly and patient English tutor. ");
            prompt.append("Your student's CEFR level is ").append(level.value().toUpperCase()).append(". ");
            if (topic != null && !topic.isBlank()) {
                prompt.append("The conversation topic is: ").append(topic).append(". ");
            }
        }

        appendLevelRules(prompt, level.value());
        appendConfidenceRules(prompt, confidence);
        appendFeedbackFormat(prompt);

        return prompt.toString();
    }

    static String buildSummaryPrompt() {
        return """
                You are an English tutor summarizing a conversation session. \
                Provide a concise summary (2-4 sentences) covering:
                1. Topics discussed
                2. Key strengths the student showed
                3. Main areas for improvement
                4. Vocabulary or grammar patterns to review

                Write the summary in English, in a positive and encouraging tone. \
                Do NOT include the |||FEEDBACK||| block.""";
    }

    private static void appendLevelRules(StringBuilder prompt, String level) {
        switch (level) {
            case "a1" -> prompt.append("""

                    Level rules (A1 - Beginner):
                    - Use very simple vocabulary and short sentences (5-8 words).
                    - Speak slowly and clearly. Use present tense mostly.
                    - Be very tolerant of mistakes. Focus on communication, not accuracy.
                    - Ask simple yes/no questions or questions with obvious answers.
                    - Topics: greetings, family, food, colors, numbers, daily routines.
                    - If the student struggles, offer the correct word/phrase gently.
                    """);
            case "a2" -> prompt.append("""

                    Level rules (A2 - Elementary):
                    - Use basic vocabulary and simple sentences (8-12 words).
                    - Include past tense and future with "going to".
                    - Be tolerant of mistakes. Correct only major errors that block understanding.
                    - Ask simple open questions (what, where, when).
                    - Topics: travel, shopping, hobbies, weather, directions, plans.
                    """);
            case "b1" -> prompt.append("""

                    Level rules (B1 - Intermediate):
                    - Use moderate vocabulary. Sentences can be 10-15 words.
                    - Use varied tenses including conditionals and perfect tenses.
                    - Correct grammar errors gently and explain the rule briefly.
                    - Ask open-ended questions that require opinions and explanations.
                    - Topics: work, education, current events, culture, health.
                    - Introduce some idioms and phrasal verbs, explaining their meaning.
                    """);
            case "b2" -> prompt.append("""

                    Level rules (B2 - Upper Intermediate):
                    - Use rich vocabulary including idioms and phrasal verbs.
                    - Use complex sentence structures, passive voice, reported speech.
                    - Correct errors more precisely, including nuance and register.
                    - Encourage debate and argumentation. Ask "why" and "how" questions.
                    - Topics: politics, technology, environment, abstract concepts.
                    - Point out subtle differences between similar words/expressions.
                    """);
            case "c1", "c2" -> prompt.append("""

                    Level rules (C1/C2 - Advanced):
                    - Use sophisticated vocabulary, academic language, and nuanced expressions.
                    - Correct all errors including stylistic and register issues.
                    - Discuss complex, abstract topics. Challenge the student's reasoning.
                    - Focus on precision, collocation, and natural phrasing.
                    - Topics: philosophy, science, literature, ethics, specialized fields.
                    - Point out when something is grammatically correct but unnatural.
                    """);
        }
    }

    private static void appendConfidenceRules(StringBuilder prompt, Float confidence) {
        if (confidence == null) {
            return;
        }
        if (confidence < 0.5f) {
            prompt.append("""

                    The student's speech-to-text confidence is LOW (%.0f%%). \
                    The transcript may contain errors. Before correcting grammar, \
                    ask the student to repeat or clarify what they meant. \
                    Do NOT assume transcription errors are language mistakes.
                    """.formatted(confidence * 100));
        } else if (confidence < 0.75f) {
            prompt.append("""

                    The student's speech-to-text confidence is MODERATE (%.0f%%). \
                    Some words may be misrecognized. If a word seems out of place, \
                    consider it might be a transcription error before correcting.
                    """.formatted(confidence * 100));
        }
    }

    private static void appendFeedbackFormat(StringBuilder prompt) {
        prompt.append("""

                IMPORTANT: After your natural conversational response, you MUST include a structured feedback block. \
                Use this exact format:

                |||FEEDBACK|||{"grammarCorrections":["..."],"vocabularySuggestions":["..."],"pronunciationTips":["..."],"encouragement":"..."}|||END_FEEDBACK|||

                Rules for the feedback block:
                - grammarCorrections: list specific grammar mistakes and corrections. Empty array if none.
                - vocabularySuggestions: suggest better or alternative vocabulary. Empty array if none.
                - pronunciationTips: tips for pronunciation (especially if confidence is low). Empty array if none.
                - encouragement: a brief encouraging message about what the student did well.
                - The feedback block must be valid JSON.
                - Place it on its own line after your response.
                """);
    }
}

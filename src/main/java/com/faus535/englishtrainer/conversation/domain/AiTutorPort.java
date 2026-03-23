package com.faus535.englishtrainer.conversation.domain;

import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;

import java.util.List;

public interface AiTutorPort {

    AiTutorResponse chat(ConversationLevel level, String topic, List<ConversationTurn> turns,
                         Float confidence) throws AiTutorException;

    String summarize(ConversationLevel level, List<ConversationTurn> turns) throws AiTutorException;

    ConversationEvaluation evaluate(ConversationLevel level, List<ConversationTurn> turns,
                                     List<ConversationGoal> goals) throws AiTutorException;

    List<ConversationGoal> generateGoals(ConversationLevel level, String topic) throws AiTutorException;

    record AiTutorResponse(String content, TutorFeedback feedback) {}
}

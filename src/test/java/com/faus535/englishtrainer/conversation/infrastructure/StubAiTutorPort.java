package com.faus535.englishtrainer.conversation.infrastructure;

import com.faus535.englishtrainer.conversation.domain.*;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;

import java.util.List;

public final class StubAiTutorPort implements AiTutorPort {

    private String nextResponse = "Hello! How are you today?";
    private TutorFeedback nextFeedback = TutorFeedback.empty();
    private String nextSummary = "Good conversation session.";
    private boolean shouldFail = false;

    @Override
    public AiTutorResponse chat(ConversationLevel level, String topic,
                                 List<ConversationTurn> turns, Float confidence) throws AiTutorException {
        if (shouldFail) {
            throw new AiTutorException("Stub AI failure");
        }
        return new AiTutorResponse(nextResponse, nextFeedback);
    }

    @Override
    public String summarize(ConversationLevel level, List<ConversationTurn> turns) throws AiTutorException {
        if (shouldFail) {
            throw new AiTutorException("Stub AI failure");
        }
        return nextSummary;
    }

    public void willRespond(String response, TutorFeedback feedback) {
        this.nextResponse = response;
        this.nextFeedback = feedback;
    }

    public void willSummarize(String summary) {
        this.nextSummary = summary;
    }

    @Override
    public ConversationEvaluation evaluate(ConversationLevel level, List<ConversationTurn> turns,
                                            List<ConversationGoal> goals) {
        return ConversationEvaluation.empty();
    }

    @Override
    public List<ConversationGoal> generateGoals(ConversationLevel level, String topic) {
        return List.of();
    }

    public void willFail() {
        this.shouldFail = true;
    }
}

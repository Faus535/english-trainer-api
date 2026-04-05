package com.faus535.englishtrainer.talk.infrastructure;

import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;

import java.util.List;

public class StubTalkAiPort implements TalkAiPort {

    private TalkCorrection correctionToReturn = TalkCorrection.empty();
    private TalkEvaluation evaluationToReturn = new TalkEvaluation(
            80, 70, 75, 85, 78, "b1", List.of("Good structure"), List.of("More vocabulary"));

    public void setCorrectionToReturn(TalkCorrection correction) {
        this.correctionToReturn = correction;
    }

    public void setEvaluationToReturn(TalkEvaluation evaluation) {
        this.evaluationToReturn = evaluation;
    }

    @Override
    public TalkAiResponse chat(TalkLevel level, TalkScenario scenario, List<TalkMessage> messages,
                                Float confidence) throws TalkAiException {
        if (messages.isEmpty()) {
            return new TalkAiResponse("Hello! Welcome to our conversation.", TalkCorrection.empty());
        }
        return new TalkAiResponse("That's interesting! Tell me more.", correctionToReturn);
    }

    @Override
    public String summarize(TalkLevel level, List<TalkMessage> messages) throws TalkAiException {
        return "You practiced conversation about daily topics. Good progress!";
    }

    @Override
    public TalkEvaluation evaluate(TalkLevel level, List<TalkMessage> messages) throws TalkAiException {
        return evaluationToReturn;
    }
}

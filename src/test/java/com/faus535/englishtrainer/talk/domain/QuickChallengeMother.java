package com.faus535.englishtrainer.talk.domain;

public final class QuickChallengeMother {

    public static QuickChallenge easy() {
        return new QuickChallenge("order-coffee", "Order a Coffee",
                "Order your favourite drink at a café", QuickChallengeDifficulty.EASY, QuickChallengeCategory.SHOPPING);
    }

    public static QuickChallenge hard() {
        return new QuickChallenge("billing-error", "Resolve a Billing Error",
                "Dispute a charge politely", QuickChallengeDifficulty.HARD, QuickChallengeCategory.PROBLEM_SOLVING);
    }
}

package com.faus535.englishtrainer.talk.domain;

import java.util.List;

import static com.faus535.englishtrainer.talk.domain.QuickChallengeCategory.*;
import static com.faus535.englishtrainer.talk.domain.QuickChallengeDifficulty.*;

public final class QuickChallenges {

    public static final List<QuickChallenge> ALL = List.of(
            new QuickChallenge("order-coffee", "Order a Coffee", "Order your favourite drink at a café", EASY, SHOPPING),
            new QuickChallenge("buy-groceries", "Buy Groceries", "Ask for items and check out", EASY, SHOPPING),
            new QuickChallenge("phone-appointment", "Make a Phone Appointment", "Call to schedule a doctor visit", MEDIUM, SOCIAL),
            new QuickChallenge("talk-weekend", "Talk About Your Weekend", "Chat casually about recent activities", EASY, SOCIAL),
            new QuickChallenge("describe-home", "Describe Your Home", "Walk someone through your living space", EASY, SOCIAL),
            new QuickChallenge("ask-directions", "Ask for Directions", "Find your way in an unfamiliar area", EASY, TRAVEL),
            new QuickChallenge("hotel-checkin", "Check In at a Hotel", "Handle check-in process", MEDIUM, TRAVEL),
            new QuickChallenge("restaurant-order", "Order at a Restaurant", "Full ordering interaction", EASY, SHOPPING),
            new QuickChallenge("billing-error", "Resolve a Billing Error", "Dispute a charge politely", HARD, PROBLEM_SOLVING),
            new QuickChallenge("neighbour-problem", "Explain a Problem to a Neighbour", "Address a noise/parking issue", MEDIUM, SOCIAL),
            new QuickChallenge("return-product", "Return a Faulty Product", "Handle a shop return", MEDIUM, PROBLEM_SOLVING),
            new QuickChallenge("delayed-flight", "Deal with a Delayed Flight", "Negotiate at the airport desk", HARD, TRAVEL),
            new QuickChallenge("room-upgrade", "Negotiate a Room Upgrade", "Ask for a better room politely", HARD, TRAVEL),
            new QuickChallenge("new-job-intro", "Introduce Yourself at a New Job", "First day small talk", MEDIUM, PROFESSIONAL),
            new QuickChallenge("propose-idea", "Propose an Idea in a Meeting", "Present a suggestion confidently", HARD, PROFESSIONAL)
    );

    private QuickChallenges() {}
}

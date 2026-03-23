package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.domain.AiTutorPort;
import com.faus535.englishtrainer.conversation.domain.ConversationGoal;
import com.faus535.englishtrainer.conversation.domain.ConversationLevel;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class SuggestGoalsController {

    private final AiTutorPort aiTutorPort;

    SuggestGoalsController(AiTutorPort aiTutorPort) {
        this.aiTutorPort = aiTutorPort;
    }

    @GetMapping("/api/conversations/suggested-goals")
    ResponseEntity<List<ConversationGoal>> handle(
            @RequestParam String level,
            @RequestParam(required = false) String topic) throws AiTutorException {

        ConversationLevel conversationLevel = new ConversationLevel(level);
        List<ConversationGoal> goals = aiTutorPort.generateGoals(conversationLevel, topic);
        return ResponseEntity.ok(goals);
    }
}

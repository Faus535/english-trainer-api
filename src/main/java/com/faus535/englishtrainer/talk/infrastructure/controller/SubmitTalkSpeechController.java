package com.faus535.englishtrainer.talk.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class SubmitTalkSpeechController {

    @PostMapping("/api/talk/conversations/{id}/speech")
    ResponseEntity<Map<String, String>> handle(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Speech analysis coming soon"));
    }
}

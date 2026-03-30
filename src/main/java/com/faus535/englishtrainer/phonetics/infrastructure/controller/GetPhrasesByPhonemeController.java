package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.application.GetPhrasesByPhonemeUseCase;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhrase;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetPhrasesByPhonemeController {
    private final GetPhrasesByPhonemeUseCase useCase;

    GetPhrasesByPhonemeController(GetPhrasesByPhonemeUseCase useCase) {
        this.useCase = useCase;
    }

    record PhraseResponse(String id, String text, String difficulty,
                           List<String> targetWords, String phonemeId) {}

    @GetMapping("/api/phonetics/phonemes/{phonemeId}/phrases")
    ResponseEntity<List<PhraseResponse>> handle(@PathVariable UUID phonemeId)
            throws PhonemeNotFoundException {
        List<PhraseResponse> response = useCase.execute(new PhonemeId(phonemeId)).stream()
                .map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    private PhraseResponse toResponse(PhonemePracticePhrase p) {
        return new PhraseResponse(
            p.id().value().toString(), p.text(), p.difficulty(),
            p.targetWords(), p.phonemeId().value().toString()
        );
    }
}

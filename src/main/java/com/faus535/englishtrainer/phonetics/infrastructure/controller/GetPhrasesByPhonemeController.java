package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.application.GetPhrasesByPhonemeUseCase;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhrase;
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

    record PhraseDto(String id, String phonemeId, String text, String difficulty, List<String> targetWords) {}

    @GetMapping("/api/phonetics/phonemes/{phonemeId}/phrases")
    ResponseEntity<List<PhraseDto>> handle(@PathVariable UUID phonemeId) {
        List<PhraseDto> response = useCase.execute(new PhonemeId(phonemeId)).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    private PhraseDto toDto(PhonemePracticePhrase p) {
        return new PhraseDto(
                p.id().value().toString(), p.phonemeId().value().toString(),
                p.text(), p.difficulty(), p.targetWords()
        );
    }
}

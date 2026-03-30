package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.application.GetPhonemeByIdUseCase;
import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetPhonemeByIdController {

    private final GetPhonemeByIdUseCase useCase;

    GetPhonemeByIdController(GetPhonemeByIdUseCase useCase) {
        this.useCase = useCase;
    }

    record PhonemeDto(String id, String symbol, String name, String category,
                      String subcategory, int difficultyOrder, List<String> exampleWords,
                      String description, String mouthPosition, String tips) {}

    @GetMapping("/api/phonetics/phonemes/{phonemeId}")
    ResponseEntity<PhonemeDto> handle(@PathVariable UUID phonemeId) throws PhonemeNotFoundException {
        Phoneme p = useCase.execute(new PhonemeId(phonemeId));
        return ResponseEntity.ok(new PhonemeDto(
                p.id().value().toString(), p.symbol(), p.name(),
                p.category().value(), p.subcategory(), p.difficultyOrder(),
                p.exampleWords(), p.description(), p.mouthPosition(), p.tips()
        ));
    }
}

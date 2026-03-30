package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.application.GetAllPhonemesUseCase;
import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetAllPhonemesController {

    private final GetAllPhonemesUseCase useCase;

    GetAllPhonemesController(GetAllPhonemesUseCase useCase) {
        this.useCase = useCase;
    }

    record PhonemeDto(String id, String symbol, String name, String category,
                      String subcategory, int difficultyOrder, List<String> exampleWords,
                      String description, String mouthPosition, String tips) {}

    @GetMapping("/api/phonetics/phonemes")
    ResponseEntity<List<PhonemeDto>> handle() {
        List<PhonemeDto> response = useCase.execute().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    private PhonemeDto toDto(Phoneme p) {
        return new PhonemeDto(
                p.id().value().toString(), p.symbol(), p.name(),
                p.category().value(), p.subcategory(), p.difficultyOrder(),
                p.exampleWords(), p.description(), p.mouthPosition(), p.tips()
        );
    }
}

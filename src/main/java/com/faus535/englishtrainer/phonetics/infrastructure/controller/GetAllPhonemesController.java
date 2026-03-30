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

    record PhonemeResponse(String id, String symbol, String name, String category,
                           String subcategory, List<String> exampleWords, String description) {}

    @GetMapping("/api/phonetics/phonemes")
    ResponseEntity<List<PhonemeResponse>> handle() {
        List<PhonemeResponse> response = useCase.execute().stream()
                .map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    private PhonemeResponse toResponse(Phoneme p) {
        return new PhonemeResponse(
            p.id().value().toString(), p.symbol(), p.name(),
            p.category().value(), p.subcategory(), p.exampleWords(), p.description()
        );
    }
}

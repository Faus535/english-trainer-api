package com.faus535.englishtrainer.vocabulary.infrastructure;

import com.faus535.englishtrainer.vocabulary.application.GetAllVocabUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetAllVocabController {

    private final GetAllVocabUseCase useCase;

    GetAllVocabController(GetAllVocabUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/vocab")
    List<VocabEntry> execute() {
        return useCase.execute();
    }
}

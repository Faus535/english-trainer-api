package com.faus535.englishtrainer.vocabulary.infrastructure;

import com.faus535.englishtrainer.vocabulary.application.GetVocabByLevelUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetVocabByLevelController {

    private final GetVocabByLevelUseCase useCase;

    GetVocabByLevelController(GetVocabByLevelUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/vocab/level/{level}")
    List<VocabEntry> execute(@PathVariable String level) {
        return useCase.execute(level);
    }
}

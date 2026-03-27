package com.faus535.englishtrainer.minigame.infrastructure.controller;

import com.faus535.englishtrainer.minigame.application.GetFillGapDataUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
class GetFillGapDataController {

    private final GetFillGapDataUseCase useCase;

    GetFillGapDataController(GetFillGapDataUseCase useCase) {
        this.useCase = useCase;
    }

    record FillGapSentenceResponse(String sentence, String blank, List<String> options, int correct) {}
    record FillGapDataResponse(List<FillGapSentenceResponse> sentences, String level) {}

    @GetMapping("/api/minigames/fill-gap")
    ResponseEntity<FillGapDataResponse> handle(@RequestParam String level) {
        VocabLevel vocabLevel = new VocabLevel(level);
        List<GetFillGapDataUseCase.FillGapItem> items = useCase.execute(vocabLevel);

        List<FillGapSentenceResponse> sentences = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < items.size(); i++) {
            GetFillGapDataUseCase.FillGapItem item = items.get(i);
            String[] words = item.en().split("\\s+");
            if (words.length < 2) continue;

            String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z']", "");
            String sentenceWithBlank = String.join(" ", Arrays.copyOf(words, words.length - 1)) + " ___";

            List<String> distractors = items.stream()
                    .filter(other -> !other.equals(item))
                    .map(other -> {
                        String[] otherWords = other.en().split("\\s+");
                        return otherWords[otherWords.length - 1].replaceAll("[^a-zA-Z']", "");
                    })
                    .filter(w -> !w.equalsIgnoreCase(lastWord))
                    .distinct()
                    .limit(3)
                    .collect(Collectors.toList());

            List<String> options = new ArrayList<>(distractors);
            options.add(lastWord);
            Collections.shuffle(options, random);

            int correctIndex = options.indexOf(lastWord);

            sentences.add(new FillGapSentenceResponse(sentenceWithBlank, lastWord, options, correctIndex));
        }

        return ResponseEntity.ok(new FillGapDataResponse(sentences, level));
    }
}

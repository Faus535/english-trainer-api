package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetImmerseVocabularyUseCase {

    private final ImmerseContentRepository repository;

    GetImmerseVocabularyUseCase(ImmerseContentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<VocabularyItem> execute(UUID contentId) throws ImmerseContentNotFoundException {
        ImmerseContentId id = new ImmerseContentId(contentId);
        ImmerseContent content = repository.findById(id)
                .orElseThrow(() -> new ImmerseContentNotFoundException(id));
        return content.extractedVocabulary();
    }
}

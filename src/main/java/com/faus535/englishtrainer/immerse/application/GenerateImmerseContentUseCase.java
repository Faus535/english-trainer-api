package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseAiException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@UseCase
public class GenerateImmerseContentUseCase {

    private final ImmerseContentRepository contentRepository;
    private final ImmerseExerciseRepository exerciseRepository;
    private final ImmerseAiPort aiPort;
    private final UserProfileRepository userProfileRepository;

    GenerateImmerseContentUseCase(ImmerseContentRepository contentRepository,
                                   ImmerseExerciseRepository exerciseRepository,
                                   ImmerseAiPort aiPort,
                                   UserProfileRepository userProfileRepository) {
        this.contentRepository = contentRepository;
        this.exerciseRepository = exerciseRepository;
        this.aiPort = aiPort;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public ImmerseContent execute(UUID userId, ContentType contentType, String level, String topic)
            throws ImmerseAiException, UserProfileNotFoundException {

        userProfileRepository.findById(new UserProfileId(userId))
                .orElseThrow(() -> new UserProfileNotFoundException(new UserProfileId(userId)));

        ImmerseContent content = ImmerseContent.generate(userId, contentType, level, topic);
        ImmerseContent saved = contentRepository.save(content);

        return saved;
    }
}

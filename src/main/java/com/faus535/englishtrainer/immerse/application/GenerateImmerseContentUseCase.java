package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ContentType;
import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class GenerateImmerseContentUseCase {

    private final ImmerseContentRepository contentRepository;
    private final ProcessImmerseContentAsyncService asyncService;
    private final UserProfileRepository userProfileRepository;

    GenerateImmerseContentUseCase(ImmerseContentRepository contentRepository,
                                   ProcessImmerseContentAsyncService asyncService,
                                   UserProfileRepository userProfileRepository) {
        this.contentRepository = contentRepository;
        this.asyncService = asyncService;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public ImmerseContent execute(UUID userId, ContentType contentType, String level, String topic)
            throws UserProfileNotFoundException {

        userProfileRepository.findById(new UserProfileId(userId))
                .orElseThrow(() -> new UserProfileNotFoundException(new UserProfileId(userId)));

        ImmerseContent content = ImmerseContent.generate(userId, contentType, level, topic);
        ImmerseContent saved = contentRepository.save(content);

        asyncService.process(saved.id().value(), contentType, level, topic);

        return saved;
    }
}

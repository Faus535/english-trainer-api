package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.learningpath.application.GetNextContentUseCase;
import com.faus535.englishtrainer.learningpath.domain.ContentType;
import com.faus535.englishtrainer.session.domain.ModuleWeight;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionBlock;
import com.faus535.englishtrainer.session.domain.SessionExercise;
import com.faus535.englishtrainer.session.domain.SessionGenerator;
import com.faus535.englishtrainer.session.domain.SessionMode;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.session.domain.error.ActiveSessionExistsException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UseCase
public class GenerateSessionUseCase {

    private static final Map<String, ContentType> MODULE_TO_CONTENT_TYPE = Map.of(
            "vocabulary", ContentType.VOCAB,
            "grammar", ContentType.GRAMMAR,
            "phrases", ContentType.PHRASE,
            "reading", ContentType.READING,
            "writing", ContentType.WRITING
    );

    private final UserProfileRepository userProfileRepository;
    private final SessionRepository sessionRepository;
    private final GetNextContentUseCase getNextContentUseCase;

    public GenerateSessionUseCase(UserProfileRepository userProfileRepository,
                                  SessionRepository sessionRepository,
                                  GetNextContentUseCase getNextContentUseCase) {
        this.userProfileRepository = userProfileRepository;
        this.sessionRepository = sessionRepository;
        this.getNextContentUseCase = getNextContentUseCase;
    }

    @Transactional
    public Session execute(UserProfileId userId, SessionMode mode) throws UserProfileNotFoundException, ActiveSessionExistsException {
        return execute(userId, mode, null);
    }

    @Transactional
    public Session execute(UserProfileId userId, SessionMode mode, List<ModuleWeight> weights)
            throws UserProfileNotFoundException, ActiveSessionExistsException {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(userId));

        if (sessionRepository.findActiveByUser(userId).isPresent()) {
            throw new ActiveSessionExistsException();
        }

        int sessionCount = profile.sessionCount();
        Session session;

        if (SessionGenerator.shouldBeIntegrator(sessionCount)) {
            String theme = "integration-review-" + sessionCount;
            session = SessionGenerator.generateIntegrator(userId, mode, theme);
        } else {
            session = SessionGenerator.generateNormal(userId, mode, sessionCount, weights);
        }

        List<SessionBlock> enrichedBlocks = enrichBlocksWithContent(userId, session.blocks());
        List<SessionExercise> exercises = SessionGenerator.buildExercises(enrichedBlocks);

        session = Session.create(userId, mode, session.sessionType(),
                session.listeningModule(), session.secondaryModule(),
                session.integratorTheme(), enrichedBlocks, exercises);

        return sessionRepository.save(session);
    }

    private List<SessionBlock> enrichBlocksWithContent(UserProfileId userId, List<SessionBlock> blocks) {
        List<SessionBlock> enriched = new ArrayList<>();
        for (SessionBlock block : blocks) {
            int exerciseCount = SessionGenerator.calculateExerciseCount(
                    block.moduleName(), block.durationMinutes());
            ContentType contentType = MODULE_TO_CONTENT_TYPE.get(block.moduleName());
            List<UUID> contentIds = getNextContentUseCase.execute(userId, contentType, exerciseCount);
            enriched.add(SessionGenerator.enrichBlock(block, contentIds));
        }
        return enriched;
    }
}

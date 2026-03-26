package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.BlockProgress;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.session.domain.error.BlockNotCompletedException;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class AdvanceBlockUseCase {

    private final SessionRepository sessionRepository;

    public AdvanceBlockUseCase(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public record AdvanceBlockResult(
            int blockIndex,
            boolean blockCompleted,
            Integer nextBlockIndex,
            boolean sessionCompleted,
            int completedExercises,
            int totalExercises
    ) {}

    @Transactional(readOnly = true)
    public AdvanceBlockResult execute(UserProfileId userId, SessionId sessionId, int blockIndex)
            throws SessionNotFoundException, BlockNotCompletedException {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        if (!session.userId().equals(userId)) {
            throw new SessionNotFoundException(sessionId);
        }

        if (!session.isBlockCompleted(blockIndex)) {
            throw new BlockNotCompletedException(blockIndex);
        }

        BlockProgress progress = session.getBlockProgress(blockIndex);
        boolean sessionCompleted = session.areAllBlocksCompleted();
        Integer nextBlockIndex = blockIndex + 1 < session.blocks().size() ? blockIndex + 1 : null;

        return new AdvanceBlockResult(
                blockIndex,
                true,
                nextBlockIndex,
                sessionCompleted,
                progress.completedExercises(),
                progress.totalExercises()
        );
    }
}

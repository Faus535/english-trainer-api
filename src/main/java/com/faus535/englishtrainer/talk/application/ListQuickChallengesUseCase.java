package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.QuickChallenge;
import com.faus535.englishtrainer.talk.domain.QuickChallenges;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@UseCase
public class ListQuickChallengesUseCase {

    private static final int DAILY_COUNT = 3;

    @Transactional(readOnly = true)
    public List<QuickChallenge> execute(LocalDate date) {
        List<QuickChallenge> all = QuickChallenges.ALL;
        int offset = date.getDayOfYear() % (all.size() - DAILY_COUNT + 1);
        return all.subList(offset, offset + DAILY_COUNT);
    }
}

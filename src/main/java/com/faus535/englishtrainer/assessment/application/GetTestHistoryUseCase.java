package com.faus535.englishtrainer.assessment.application;

import com.faus535.englishtrainer.assessment.domain.LevelTestResult;
import com.faus535.englishtrainer.assessment.domain.LevelTestResultRepository;
import com.faus535.englishtrainer.assessment.domain.MiniTestResult;
import com.faus535.englishtrainer.assessment.domain.MiniTestResultRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetTestHistoryUseCase {

    private final LevelTestResultRepository levelTestResultRepository;
    private final MiniTestResultRepository miniTestResultRepository;

    public GetTestHistoryUseCase(LevelTestResultRepository levelTestResultRepository,
                                  MiniTestResultRepository miniTestResultRepository) {
        this.levelTestResultRepository = levelTestResultRepository;
        this.miniTestResultRepository = miniTestResultRepository;
    }

    @Transactional(readOnly = true)
    public TestHistory execute(UserProfileId userId) {
        List<LevelTestResult> levelTests = levelTestResultRepository.findByUser(userId);
        List<MiniTestResult> miniTests = miniTestResultRepository.findByUser(userId);
        return new TestHistory(levelTests, miniTests);
    }

    public record TestHistory(List<LevelTestResult> levelTests, List<MiniTestResult> miniTests) {
    }
}

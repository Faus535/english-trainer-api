package com.faus535.englishtrainer.moduleprogress.application;

import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgressRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetAllModuleProgressUseCase {

    private final ModuleProgressRepository repository;

    public GetAllModuleProgressUseCase(ModuleProgressRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ModuleProgress> execute(UserProfileId userId) {
        return repository.findAllByUser(userId);
    }
}

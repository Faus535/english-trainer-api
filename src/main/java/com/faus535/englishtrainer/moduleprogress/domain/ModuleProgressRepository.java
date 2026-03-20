package com.faus535.englishtrainer.moduleprogress.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;
import java.util.Optional;

public interface ModuleProgressRepository {

    Optional<ModuleProgress> findByUserAndModuleAndLevel(UserProfileId userId, ModuleName moduleName, ModuleLevel level);

    List<ModuleProgress> findAllByUser(UserProfileId userId);

    ModuleProgress save(ModuleProgress progress);
}

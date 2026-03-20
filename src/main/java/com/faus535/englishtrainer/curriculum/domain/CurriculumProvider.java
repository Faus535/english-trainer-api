package com.faus535.englishtrainer.curriculum.domain;

import java.util.List;
import java.util.Optional;

public interface CurriculumProvider {

    List<CurriculumBlock> getPlan();

    Optional<CurriculumBlock> getBlock(int blockNumber);

    List<ModuleDefinition> getModules();

    Optional<ModuleDefinition> getModule(String name);

    List<IntegratorDefinition> getIntegrators(String level);

    List<IntegratorDefinition> getAllIntegrators();
}

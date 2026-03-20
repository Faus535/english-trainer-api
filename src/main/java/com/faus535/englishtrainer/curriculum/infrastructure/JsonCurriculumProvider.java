package com.faus535.englishtrainer.curriculum.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.curriculum.domain.CurriculumBlock;
import com.faus535.englishtrainer.curriculum.domain.CurriculumProvider;
import com.faus535.englishtrainer.curriculum.domain.IntegratorDefinition;
import com.faus535.englishtrainer.curriculum.domain.ModuleDefinition;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class JsonCurriculumProvider implements CurriculumProvider {

    private final ObjectMapper objectMapper;

    private List<CurriculumBlock> plan;
    private List<ModuleDefinition> modules;
    private List<IntegratorDefinition> integrators;

    public JsonCurriculumProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void init() throws IOException {
        try (InputStream planStream = new ClassPathResource("curriculum/plan.json").getInputStream();
             InputStream modulesStream = new ClassPathResource("curriculum/modules.json").getInputStream();
             InputStream integratorsStream = new ClassPathResource("curriculum/integrators.json").getInputStream()) {

            this.plan = objectMapper.readValue(planStream, new TypeReference<>() {});
            this.modules = objectMapper.readValue(modulesStream, new TypeReference<>() {});
            this.integrators = objectMapper.readValue(integratorsStream, new TypeReference<>() {});
        }
    }

    @Override
    public List<CurriculumBlock> getPlan() {
        return plan;
    }

    @Override
    public Optional<CurriculumBlock> getBlock(int blockNumber) {
        return plan.stream()
                .filter(block -> block.blockNumber() == blockNumber)
                .findFirst();
    }

    @Override
    public List<ModuleDefinition> getModules() {
        return modules;
    }

    @Override
    public Optional<ModuleDefinition> getModule(String name) {
        return modules.stream()
                .filter(module -> module.name().equals(name))
                .findFirst();
    }

    @Override
    public List<IntegratorDefinition> getIntegrators(String level) {
        return integrators.stream()
                .filter(integrator -> integrator.level().equals(level))
                .toList();
    }

    @Override
    public List<IntegratorDefinition> getAllIntegrators() {
        return integrators;
    }
}

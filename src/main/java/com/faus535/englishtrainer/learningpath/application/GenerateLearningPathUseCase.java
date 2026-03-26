package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.curriculum.domain.CurriculumProvider;
import com.faus535.englishtrainer.curriculum.domain.ModuleDefinition;
import com.faus535.englishtrainer.curriculum.domain.UnitDefinition;
import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningPathGenerator;
import com.faus535.englishtrainer.learningpath.domain.LearningPathRepository;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitRepository;
import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.phrase.domain.PhraseRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@UseCase
public class GenerateLearningPathUseCase {

    private final LearningPathRepository learningPathRepository;
    private final LearningUnitRepository learningUnitRepository;
    private final UserProfileRepository userProfileRepository;
    private final VocabRepository vocabRepository;
    private final PhraseRepository phraseRepository;
    private final CurriculumProvider curriculumProvider;
    private final SpacedRepetitionRepository spacedRepetitionRepository;

    public GenerateLearningPathUseCase(LearningPathRepository learningPathRepository,
                                       LearningUnitRepository learningUnitRepository,
                                       UserProfileRepository userProfileRepository,
                                       VocabRepository vocabRepository,
                                       PhraseRepository phraseRepository,
                                       CurriculumProvider curriculumProvider,
                                       SpacedRepetitionRepository spacedRepetitionRepository) {
        this.learningPathRepository = learningPathRepository;
        this.learningUnitRepository = learningUnitRepository;
        this.userProfileRepository = userProfileRepository;
        this.vocabRepository = vocabRepository;
        this.phraseRepository = phraseRepository;
        this.curriculumProvider = curriculumProvider;
        this.spacedRepetitionRepository = spacedRepetitionRepository;
    }

    @Transactional
    public GenerationSummary execute(UserProfileId userId) throws UserProfileNotFoundException {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(userId));

        String currentLevel = profile.levelVocabulary().value();
        VocabLevel vocabLevel = new VocabLevel(currentLevel);

        List<VocabEntry> vocabEntries = vocabRepository.findByLevel(vocabLevel);
        List<Phrase> phrases = phraseRepository.findByLevel(vocabLevel);

        List<UnitDefinition> curriculumUnits = loadCurriculumUnits(currentLevel);

        Set<UUID> alreadyPracticedIds = loadPracticedIds(userId);

        learningPathRepository.deleteByUserId(userId);

        LearningPathGenerator.GenerationResult result = LearningPathGenerator.generate(
                userId, currentLevel, vocabEntries, phrases, curriculumUnits, alreadyPracticedIds);

        LearningPath savedPath = learningPathRepository.save(result.path());
        learningUnitRepository.saveAll(result.units());

        return new GenerationSummary(
                savedPath.id().value(),
                savedPath.currentLevel(),
                result.units().size(),
                result.units().stream().mapToLong(u -> u.contents().size()).sum()
        );
    }

    private List<UnitDefinition> loadCurriculumUnits(String level) {
        Optional<ModuleDefinition> vocabModule = curriculumProvider.getModule("vocabulary");
        if (vocabModule.isPresent()) {
            ModuleDefinition module = vocabModule.get();
            List<UnitDefinition> units = module.levels().get(level.toUpperCase());
            if (units != null) {
                return units;
            }
            units = module.levels().get(level.toLowerCase());
            if (units != null) {
                return units;
            }
        }
        return List.of();
    }

    private Set<UUID> loadPracticedIds(UserProfileId userId) {
        List<SpacedRepetitionItem> items = spacedRepetitionRepository.findAllByUser(userId);
        Set<UUID> practicedIds = new HashSet<>();
        for (SpacedRepetitionItem item : items) {
            practicedIds.add(item.id().value());
        }
        return practicedIds;
    }

    public record GenerationSummary(UUID pathId, String level, int unitCount, long totalContentItems) {}
}

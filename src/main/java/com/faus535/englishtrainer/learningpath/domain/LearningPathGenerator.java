package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.curriculum.domain.UnitDefinition;
import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class LearningPathGenerator {

    private static final int MAX_VOCAB_PER_UNIT = 20;

    private LearningPathGenerator() {}

    public static GenerationResult generate(UserProfileId userId,
                                             String currentLevel,
                                             List<VocabEntry> vocabEntries,
                                             List<Phrase> phrases,
                                             List<UnitDefinition> curriculumUnits,
                                             Set<UUID> alreadyPracticedIds) {

        LearningPathId pathId = LearningPathId.generate();
        List<LearningUnit> units = new ArrayList<>();

        Map<Integer, List<VocabEntry>> vocabByBlock = groupVocabByBlock(vocabEntries);

        if (vocabByBlock.isEmpty()) {
            Map<String, List<VocabEntry>> vocabByCategory = groupVocabByCategory(vocabEntries);
            int unitIndex = 0;
            for (Map.Entry<String, List<VocabEntry>> entry : vocabByCategory.entrySet()) {
                List<List<VocabEntry>> chunks = partition(entry.getValue(), MAX_VOCAB_PER_UNIT);
                for (List<VocabEntry> chunk : chunks) {
                    String unitName = buildUnitName(entry.getKey(), unitIndex, curriculumUnits);
                    List<UnitContent> contents = buildContents(chunk, List.of(), alreadyPracticedIds);
                    units.add(LearningUnit.create(pathId, unitIndex, unitName, currentLevel, contents));
                    unitIndex++;
                }
            }
        } else {
            int unitIndex = 0;
            for (Map.Entry<Integer, List<VocabEntry>> entry : vocabByBlock.entrySet()) {
                List<List<VocabEntry>> chunks = partition(entry.getValue(), MAX_VOCAB_PER_UNIT);
                for (List<VocabEntry> chunk : chunks) {
                    String unitName = buildBlockUnitName(entry.getKey(), chunk, unitIndex, curriculumUnits);
                    List<UnitContent> contents = buildContents(chunk, List.of(), alreadyPracticedIds);
                    units.add(LearningUnit.create(pathId, unitIndex, unitName, currentLevel, contents));
                    unitIndex++;
                }
            }
        }

        distributePhrases(units, phrases, alreadyPracticedIds, pathId, currentLevel);

        List<LearningUnitId> unitIds = units.stream().map(LearningUnit::id).toList();
        LearningPath path = LearningPath.create(userId, currentLevel, unitIds);

        return new GenerationResult(path, units);
    }

    private static Map<Integer, List<VocabEntry>> groupVocabByBlock(List<VocabEntry> vocabEntries) {
        Map<Integer, List<VocabEntry>> grouped = new LinkedHashMap<>();
        for (VocabEntry entry : vocabEntries) {
            if (entry.block() != null) {
                grouped.computeIfAbsent(entry.block(), k -> new ArrayList<>()).add(entry);
            }
        }
        return grouped;
    }

    private static Map<String, List<VocabEntry>> groupVocabByCategory(List<VocabEntry> vocabEntries) {
        Map<String, List<VocabEntry>> grouped = new LinkedHashMap<>();
        for (VocabEntry entry : vocabEntries) {
            String category = entry.category() != null ? entry.category() : "general";
            grouped.computeIfAbsent(category, k -> new ArrayList<>()).add(entry);
        }
        return grouped;
    }

    private static String buildUnitName(String category, int unitIndex, List<UnitDefinition> curriculumUnits) {
        if (curriculumUnits != null && unitIndex < curriculumUnits.size()) {
            return curriculumUnits.get(unitIndex).title();
        }
        return category.substring(0, 1).toUpperCase() + category.substring(1);
    }

    private static String buildBlockUnitName(int blockNumber, List<VocabEntry> chunk,
                                              int unitIndex, List<UnitDefinition> curriculumUnits) {
        if (curriculumUnits != null && unitIndex < curriculumUnits.size()) {
            return curriculumUnits.get(unitIndex).title();
        }
        String blockTitle = chunk.stream()
                .filter(v -> v.blockTitle() != null)
                .map(VocabEntry::blockTitle)
                .findFirst()
                .orElse("Block " + blockNumber);
        return blockTitle;
    }

    private static List<UnitContent> buildContents(List<VocabEntry> vocabChunk,
                                                    List<Phrase> phraseChunk,
                                                    Set<UUID> alreadyPracticedIds) {
        List<UnitContent> contents = new ArrayList<>();
        for (VocabEntry vocab : vocabChunk) {
            UUID vocabId = vocab.id().value();
            boolean practiced = alreadyPracticedIds.contains(vocabId);
            contents.add(new UnitContent(ContentType.VOCAB, vocabId, practiced,
                    practiced ? java.time.Instant.now() : null));
        }
        for (Phrase phrase : phraseChunk) {
            UUID phraseId = phrase.id().value();
            boolean practiced = alreadyPracticedIds.contains(phraseId);
            contents.add(new UnitContent(ContentType.PHRASE, phraseId, practiced,
                    practiced ? java.time.Instant.now() : null));
        }
        return contents;
    }

    private static void distributePhrases(List<LearningUnit> units, List<Phrase> phrases,
                                           Set<UUID> alreadyPracticedIds,
                                           LearningPathId pathId, String currentLevel) {
        if (phrases.isEmpty() || units.isEmpty()) {
            return;
        }

        int phrasesPerUnit = Math.max(1, phrases.size() / units.size());
        int phraseIndex = 0;

        for (int i = 0; i < units.size() && phraseIndex < phrases.size(); i++) {
            LearningUnit unit = units.get(i);
            List<UnitContent> existingContents = new ArrayList<>(unit.contents());

            int count = (i == units.size() - 1) ? phrases.size() - phraseIndex : phrasesPerUnit;
            count = Math.min(count, phrases.size() - phraseIndex);

            for (int j = 0; j < count && phraseIndex < phrases.size(); j++) {
                Phrase phrase = phrases.get(phraseIndex);
                UUID phraseId = phrase.id().value();
                boolean practiced = alreadyPracticedIds.contains(phraseId);
                existingContents.add(new UnitContent(ContentType.PHRASE, phraseId, practiced,
                        practiced ? java.time.Instant.now() : null));
                phraseIndex++;
            }

            units.set(i, LearningUnit.create(pathId, unit.unitIndex(), unit.unitName(),
                    currentLevel, existingContents));
        }
    }

    private static <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    public record GenerationResult(LearningPath path, List<LearningUnit> units) {}
}

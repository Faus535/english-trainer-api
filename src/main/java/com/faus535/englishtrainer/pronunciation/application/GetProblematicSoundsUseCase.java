package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationError;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationErrorRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UseCase
public class GetProblematicSoundsUseCase {

    private final PronunciationErrorRepository repository;

    public GetProblematicSoundsUseCase(PronunciationErrorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProblematicSound> execute(UserProfileId userId) {
        List<PronunciationError> errors = repository.findByUserId(userId);

        Map<String, List<PronunciationError>> byPhoneme = errors.stream()
                .collect(Collectors.groupingBy(PronunciationError::expectedPhoneme));

        return byPhoneme.entrySet().stream()
                .map(entry -> {
                    String phoneme = entry.getKey();
                    List<PronunciationError> phonemeErrors = entry.getValue();
                    int totalCount = phonemeErrors.stream().mapToInt(PronunciationError::occurrenceCount).sum();
                    List<String> topWords = phonemeErrors.stream()
                            .sorted(Comparator.comparingInt(PronunciationError::occurrenceCount).reversed())
                            .map(PronunciationError::word)
                            .distinct()
                            .limit(3)
                            .toList();
                    String lastOccurred = phonemeErrors.stream()
                            .map(PronunciationError::lastOccurred)
                            .max(java.time.Instant::compareTo)
                            .map(Object::toString)
                            .orElse(null);
                    return new ProblematicSound(phoneme, totalCount, topWords, lastOccurred);
                })
                .sorted(Comparator.comparingInt(ProblematicSound::errorCount).reversed())
                .toList();
    }

    public record ProblematicSound(String phoneme, int errorCount, List<String> topWords, String lastOccurred) {}
}

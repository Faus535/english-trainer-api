package com.faus535.englishtrainer.dailychallenge.application;

import com.faus535.englishtrainer.dailychallenge.domain.ChallengeType;
import com.faus535.englishtrainer.dailychallenge.domain.DailyChallenge;
import com.faus535.englishtrainer.dailychallenge.domain.DailyChallengeId;
import com.faus535.englishtrainer.dailychallenge.domain.DailyChallengeRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@UseCase
public class GetTodayChallengeUseCase {

    private final DailyChallengeRepository dailyChallengeRepository;

    public GetTodayChallengeUseCase(DailyChallengeRepository dailyChallengeRepository) {
        this.dailyChallengeRepository = dailyChallengeRepository;
    }

    @Transactional
    public DailyChallenge execute() {
        LocalDate today = LocalDate.now();
        return dailyChallengeRepository.findByDate(today)
                .orElseGet(() -> generateAndSave(today));
    }

    private DailyChallenge generateAndSave(LocalDate date) {
        ChallengeType[] types = ChallengeType.values();
        ChallengeType type = types[ThreadLocalRandom.current().nextInt(types.length)];

        String descriptionEs;
        String descriptionEn;
        int target;

        switch (type) {
            case FLASHCARDS -> {
                target = 10;
                descriptionEn = "Review 10 flashcards";
                descriptionEs = "Revisa 10 tarjetas";
            }
            case SPEAK_MINUTES -> {
                target = 5;
                descriptionEn = "Speak for 5 minutes";
                descriptionEs = "Habla 5 minutos";
            }
            case TUTOR_MESSAGES -> {
                target = 8;
                descriptionEn = "Send 8 messages to the tutor";
                descriptionEs = "Envia 8 mensajes al tutor";
            }
            case SESSION_COMPLETE -> {
                target = 1;
                descriptionEn = "Complete a session";
                descriptionEs = "Completa una sesion";
            }
            case MINI_GAME -> {
                target = 3;
                descriptionEn = "Play 3 mini-games";
                descriptionEs = "Juega 3 mini-juegos";
            }
            default -> {
                target = 1;
                descriptionEn = "Complete a session";
                descriptionEs = "Completa una sesion";
            }
        }

        DailyChallenge challenge = DailyChallenge.create(
                DailyChallengeId.generate(),
                type,
                descriptionEs,
                descriptionEn,
                target,
                25,
                date
        );

        return dailyChallengeRepository.save(challenge);
    }
}

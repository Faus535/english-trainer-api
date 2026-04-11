package com.faus535.englishtrainer.home.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetHomeUseCase {

    GetHomeUseCase() {}

    public HomeData execute(UUID userId) {
        return new HomeData(0, 0, new boolean[7], HomeData.SuggestedModule.TALK, 0, List.of(), null);
    }
}

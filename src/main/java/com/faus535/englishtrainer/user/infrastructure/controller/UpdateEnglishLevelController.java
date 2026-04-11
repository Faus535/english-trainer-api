package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.application.UpdateEnglishLevelUseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.domain.vo.EnglishLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class UpdateEnglishLevelController {

    private final UpdateEnglishLevelUseCase useCase;

    UpdateEnglishLevelController(UpdateEnglishLevelUseCase useCase) {
        this.useCase = useCase;
    }

    record UpdateEnglishLevelRequest(@NotNull EnglishLevel level) {}

    @PutMapping("/api/profiles/{id}/english-level")
    @RequireProfileOwnership(pathVariable = "id")
    ResponseEntity<Void> handle(@PathVariable UUID id,
                                @Valid @RequestBody UpdateEnglishLevelRequest request,
                                Authentication authentication) throws UserProfileNotFoundException {
        useCase.execute(new UserProfileId(id), request.level());
        return ResponseEntity.noContent().build();
    }
}

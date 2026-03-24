package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.SetAllLevelsUseCase;
import com.faus535.englishtrainer.user.domain.UserLevel;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
final class SetAllLevelsController {

    private final SetAllLevelsUseCase useCase;

    SetAllLevelsController(SetAllLevelsUseCase useCase) {
        this.useCase = useCase;
    }

    record SetAllLevelsRequest(@NotEmpty Map<@NotBlank String, @NotBlank String> levels) {}

    @PutMapping("/api/profiles/{id}/levels")
    ResponseEntity<Void> handle(@PathVariable UUID id,
                                @Valid @RequestBody SetAllLevelsRequest request)
            throws UserProfileNotFoundException, InvalidModuleException {

        Map<String, UserLevel> levels = request.levels().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new UserLevel(e.getValue())));

        useCase.execute(new UserProfileId(id), levels);
        return ResponseEntity.noContent().build();
    }
}

package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.UpdateModuleLevelUseCase;
import com.faus535.englishtrainer.user.domain.UserLevel;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.ProfileOwnershipException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
final class UpdateModuleLevelController {

    private final UpdateModuleLevelUseCase useCase;

    UpdateModuleLevelController(UpdateModuleLevelUseCase useCase) {
        this.useCase = useCase;
    }

    record UpdateLevelRequest(@NotBlank String level) {}

    @PutMapping("/api/profiles/{id}/modules/{module}/level")
    ResponseEntity<Void> handle(@PathVariable UUID id, @PathVariable String module,
                                @Valid @RequestBody UpdateLevelRequest request,
                                Authentication authentication)
            throws UserProfileNotFoundException, InvalidModuleException, ProfileOwnershipException {
        ProfileOwnershipChecker.check(authentication, id);
        useCase.execute(new UserProfileId(id), module, new UserLevel(request.level()));
        return ResponseEntity.noContent().build();
    }
}

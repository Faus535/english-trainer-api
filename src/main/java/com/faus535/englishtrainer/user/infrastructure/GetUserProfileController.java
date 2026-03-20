package com.faus535.englishtrainer.user.infrastructure;

import com.faus535.englishtrainer.user.application.GetUserProfileUseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetUserProfileController {

    private final GetUserProfileUseCase useCase;

    GetUserProfileController(GetUserProfileUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{id}")
    UserProfile execute(@PathVariable UUID id) {
        return useCase.execute(id);
    }
}

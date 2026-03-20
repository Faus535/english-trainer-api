package com.faus535.englishtrainer.user.infrastructure;

import com.faus535.englishtrainer.user.application.CreateUserProfileUseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CreateUserProfileController {

    private final CreateUserProfileUseCase useCase;

    CreateUserProfileController(CreateUserProfileUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/api/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    UserProfile execute() {
        return useCase.execute();
    }
}

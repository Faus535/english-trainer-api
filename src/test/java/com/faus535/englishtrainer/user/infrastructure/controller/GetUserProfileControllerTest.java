package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.GetUserProfileUseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.vo.EnglishLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetUserProfileControllerTest {

    private GetUserProfileUseCase useCase;
    private GetUserProfileController controller;

    @BeforeEach
    void setUp() {
        useCase = mock(GetUserProfileUseCase.class);
        controller = new GetUserProfileController(useCase);
    }

    @Test
    void shouldReturnEnglishLevelWhenSet() throws Exception {
        UserProfile profile = UserProfileMother.withEnglishLevel(EnglishLevel.B1);
        when(useCase.execute(profile.id())).thenReturn(profile);

        Authentication auth = mockAuthFor(profile.id().value());
        ResponseEntity<GetUserProfileController.UserProfileResponse> response =
                controller.handle(profile.id().value(), auth);

        assertEquals("B1", response.getBody().englishLevel());
    }

    @Test
    void shouldReturnNullEnglishLevelWhenNotSet() throws Exception {
        UserProfile profile = UserProfileMother.create();
        when(useCase.execute(profile.id())).thenReturn(profile);

        Authentication auth = mockAuthFor(profile.id().value());
        ResponseEntity<GetUserProfileController.UserProfileResponse> response =
                controller.handle(profile.id().value(), auth);

        assertNull(response.getBody().englishLevel());
    }

    private Authentication mockAuthFor(UUID profileId) {
        Authentication auth = mock(Authentication.class);
        when(auth.getDetails()).thenReturn(Map.of("profileId", profileId.toString()));
        return auth;
    }
}

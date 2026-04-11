package com.faus535.englishtrainer.home;

import com.faus535.englishtrainer.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetHomeByJwtIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate rest;

    private String validToken;

    @BeforeEach
    void setUp() {
        var body = Map.of(
                "email", "home-jwt-test-" + UUID.randomUUID() + "@example.com",
                "password", "password123"
        );
        var response = rest.postForEntity("/api/auth/register", body, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        validToken = (String) response.getBody().get("token");
    }

    @Test
    void shouldReturn200WithHomeDataWhenJwtIsValid() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        var response = rest.exchange(
                "/api/home",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKeys(
                "dueReviewCount", "streakDays", "weeklyActivity",
                "suggestedModule", "recentXpThisWeek", "recentAchievements", "englishLevel"
        );
    }

    @Test
    void shouldReturn401WhenNoJwtProvided() {
        var response = rest.getForEntity("/api/home", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

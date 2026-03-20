package com.faus535.englishtrainer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class HealthCheckIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void actuator_health_returns_up() {
        var response = rest.getForEntity("/actuator/health", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }
}

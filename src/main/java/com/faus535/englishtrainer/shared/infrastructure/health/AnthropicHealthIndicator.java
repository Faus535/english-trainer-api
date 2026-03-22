package com.faus535.englishtrainer.shared.infrastructure.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
class AnthropicHealthIndicator implements HealthIndicator {

    private final String apiKey;

    AnthropicHealthIndicator(@Value("${anthropic.api-key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Health health() {
        if (apiKey == null || apiKey.isBlank()) {
            return Health.down().withDetail("reason", "API key not configured").build();
        }
        return Health.up().withDetail("status", "API key configured").build();
    }
}

package com.rere.server.domain.model.config;

import com.rere.server.domain.model.impl.ServerConfigImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

/**
 * Tests the {@link ServerConfig} class.
 */
class ServerConfigTest {

    private static final ZoneId UTC = ZoneId.of("UTC");

    @ParameterizedTest
    @ValueSource(strings = {
            "2025-12-16T00:00:00Z", // Earliest invalid expiration
            "2025-12-16T00:00:01Z",
            "2025-12-24T00:00:00Z",
            "2026-01-01T16:54:33Z",
    })
    void allowsExpirationNoticesInvalid(String instant) {
        LocalDate now = LocalDate.of(2024, 10, 12);
        Period maxExpiration = Period.of(1, 2, 3);

        ServerConfig config = ServerConfigImpl.builder()
                .maximumActivePeriod(maxExpiration)
                .build();

        Assertions.assertFalse(config.allowsExpiration(now, Instant.parse(instant), UTC));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2025-12-15T23:59:59Z", // Latest valid expiration (seconds precision)
            "2025-12-15T22:22:00Z",
            "2024-10-12T00:00:00Z",
            "2023-10-12T00:00:00Z", // Earlier values are allowed, although useless
    })
    void allowsExpirationNoticesValid(String instant) {
        LocalDate now = LocalDate.of(2024, 10, 12);
        Period maxExpiration = Period.of(1, 2, 3);

        ServerConfig config = ServerConfigImpl.builder()
                .maximumActivePeriod(maxExpiration)
                .build();

        Assertions.assertTrue(config.allowsExpiration(now, Instant.parse(instant), UTC));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2025-12-15T23:59:59Z",
            "2025-12-15T22:22:00Z",
            "2024-10-12T00:00:00Z",
            "2023-10-12T00:00:00Z",
            "2025-12-16T00:00:00Z",
            "2025-12-16T00:00:01Z",
            "2025-12-24T00:00:00Z",
            "2026-01-01T16:54:33Z",
    })
    void returnsTrueWhenNoneSet(String instant) {
        LocalDate now = LocalDate.of(2024, 10, 12);

        ServerConfig config = ServerConfigImpl.builder()
                .maximumActivePeriod(null)
                .build();

        Assertions.assertTrue(config.allowsExpiration(now, Instant.parse(instant), UTC));
    }

}

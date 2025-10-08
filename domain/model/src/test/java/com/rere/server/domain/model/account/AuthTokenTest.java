package com.rere.server.domain.model.account;

import com.rere.server.domain.model.impl.AuthTokenImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

/**
 * Tests for the {@link AuthToken} class.
 */
class AuthTokenTest {

    @ParameterizedTest
    @ValueSource(longs = {
            75847300 - 1,
            1,
            163202
    })
    void isValidFailsForExpired(long instant) {
        Instant now = Instant.ofEpochSecond(75847300);

        AuthToken token = AuthTokenImpl.builder()
                .expirationTimestamp(Instant.ofEpochSecond(instant))
                .build();

        Assertions.assertFalse(token.isValid(AuthTokenType.REFRESH_TOKEN, now));
    }

    @ParameterizedTest
    @ValueSource(longs = {
            75847300 + 1,
            99999999,
            888888888
    })
    void isValidWorksForNonExpired(long instant) {
        Instant now = Instant.ofEpochSecond(75847300);

        AuthToken token = AuthTokenImpl.builder()
                .expirationTimestamp(Instant.ofEpochSecond(instant))
                .build();

        Assertions.assertTrue(token.isValid(AuthTokenType.REFRESH_TOKEN, now));
    }

    @Test
    void isValidUsesInvalidation() {
        Instant now = Instant.ofEpochSecond(75847300);
        Instant expiration = now.plusSeconds(1000);

        AuthToken invalidatedToken = AuthTokenImpl.builder()
                .expirationTimestamp(expiration)
                .invalidated(true)
                .build();
        AuthToken notInvalidatedToken = AuthTokenImpl.builder()
                .expirationTimestamp(expiration)
                .invalidated(false)
                .build();

        Assertions.assertFalse(invalidatedToken.isValid(AuthTokenType.REFRESH_TOKEN, now));
        Assertions.assertTrue(notInvalidatedToken.isValid(AuthTokenType.REFRESH_TOKEN, now));
    }

    @Test
    void isValidUsesType() {
        Instant now = Instant.ofEpochSecond(75847300);
        Instant expiration = now.plusSeconds(1000);

        AuthToken tokenForRefresh = AuthTokenImpl.builder()
                .expirationTimestamp(expiration)
                .invalidated(false)
                .type(AuthTokenType.REFRESH_TOKEN)
                .build();

        Assertions.assertTrue(tokenForRefresh.isValid(AuthTokenType.REFRESH_TOKEN, now));
        Assertions.assertFalse(tokenForRefresh.isValid(AuthTokenType.EMAIL_VERIFICATION, now));
    }

}

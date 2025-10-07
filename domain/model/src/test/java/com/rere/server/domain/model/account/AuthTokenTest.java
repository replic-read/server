package com.rere.server.domain.model.account;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link AuthToken} class.
 */
class AuthTokenTest {

    private static Account createAccount(UUID id) {
        Account account = mock(Account.class);
        when(account.getId()).thenReturn(id);
        return account;
    }

    @ParameterizedTest
    @ValueSource(longs = {
            75847300 - 1,
            1,
            163202
    })
    void isValidFailsForExpired(long instant) {
        Instant now = Instant.ofEpochSecond(75847300);

        Account account = createAccount(UUID.randomUUID());
        AuthToken token = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), Instant.ofEpochSecond(instant), account, AuthTokenType.REFRESH_TOKEN, "", false);

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

        AuthToken token = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), Instant.ofEpochSecond(instant), createAccount(UUID.randomUUID()), AuthTokenType.REFRESH_TOKEN, "", false);

        Assertions.assertTrue(token.isValid(AuthTokenType.REFRESH_TOKEN, now));
    }

    @Test
    void isValidUsesInvalidation() {
        Account account = createAccount(UUID.randomUUID());
        Instant now = Instant.ofEpochSecond(75847300);
        Instant expiration = now.plusSeconds(1000);

        AuthToken invalidatedToken = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), expiration, account, AuthTokenType.REFRESH_TOKEN, null, true);
        AuthToken notInvalidatedToken = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), expiration, account, AuthTokenType.REFRESH_TOKEN, null, false);

        Assertions.assertFalse(invalidatedToken.isValid(AuthTokenType.REFRESH_TOKEN, now));
        Assertions.assertTrue(notInvalidatedToken.isValid(AuthTokenType.REFRESH_TOKEN, now));
    }

    @Test
    void isValidUsesType() {
        Instant now = Instant.ofEpochSecond(75847300);
        Instant expiration = now.plusSeconds(1000);
        Account account = createAccount(UUID.randomUUID());

        AuthToken tokenForRefresh = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), expiration, account, AuthTokenType.REFRESH_TOKEN, "", false);

        Assertions.assertTrue(tokenForRefresh.isValid(AuthTokenType.REFRESH_TOKEN, now));
        Assertions.assertFalse(tokenForRefresh.isValid(AuthTokenType.EMAIL_VERIFICATION, now));
    }

}

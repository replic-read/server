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
public class AuthTokenTest {

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
    public void isValidFailsForExpired(long instant) {
        Instant now = Instant.ofEpochSecond(75847300);

        Account account = createAccount(UUID.randomUUID());
        AuthToken token = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), Instant.ofEpochSecond(instant), account, AuthTokenType.REFRESH_TOKEN, "", false);

        Assertions.assertFalse(token.isValid(account, AuthTokenType.REFRESH_TOKEN, now));
    }

    @ParameterizedTest
    @ValueSource(longs = {
            75847300 + 1,
            99999999,
            888888888
    })
    public void isValidWorksForNonExpired(long instant) {
        Instant now = Instant.ofEpochSecond(75847300);

        Account account = createAccount(UUID.randomUUID());
        AuthToken token = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), Instant.ofEpochSecond(instant), account, AuthTokenType.REFRESH_TOKEN, "", false);

        Assertions.assertTrue(token.isValid(account, AuthTokenType.REFRESH_TOKEN, now));
    }

    @Test
    public void isValidUsesInvalidation() {
        Account account = createAccount(UUID.randomUUID());
        Instant now = Instant.ofEpochSecond(75847300);
        Instant expiration = now.plusSeconds(1000);

        AuthToken invalidatedToken = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), expiration, account, AuthTokenType.REFRESH_TOKEN, null, true);
        AuthToken notInvalidatedToken = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), expiration, account, AuthTokenType.REFRESH_TOKEN, null, false);

        Assertions.assertFalse(invalidatedToken.isValid(account, AuthTokenType.REFRESH_TOKEN, now));
        Assertions.assertTrue(notInvalidatedToken.isValid(account, AuthTokenType.REFRESH_TOKEN, now));
    }

    @Test
    public void isValidUsesAccountId() {
        Instant now = Instant.ofEpochSecond(75847300);
        Instant expiration = now.plusSeconds(1000);
        UUID id1 = UUID.fromString("b6c241de-b456-4a74-87c8-f3021e4ec825");
        UUID id2 = UUID.fromString("dfc8f407-224a-4ab5-b976-16fd11572863");
        UUID id3 = UUID.fromString("7c9dfc5f-bd16-4dd3-8b0d-a9c01a2f453a");
        Account acc1 = createAccount(id1);
        Account acc2 = createAccount(id2);
        Account acc3 = createAccount(id3);

        AuthToken token1 = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), expiration, acc1, AuthTokenType.REFRESH_TOKEN, "", false);

        Assertions.assertTrue(token1.isValid(acc1, AuthTokenType.REFRESH_TOKEN, now));
        Assertions.assertFalse(token1.isValid(acc2, AuthTokenType.REFRESH_TOKEN, now));
        Assertions.assertFalse(token1.isValid(acc3, AuthTokenType.REFRESH_TOKEN, now));
    }

    @Test
    public void isValidUsesType() {
        Instant now = Instant.ofEpochSecond(75847300);
        Instant expiration = now.plusSeconds(1000);
        Account account = createAccount(UUID.randomUUID());

        AuthToken tokenForRefresh = new AuthToken(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), expiration, account, AuthTokenType.REFRESH_TOKEN, "", false);

        Assertions.assertTrue(tokenForRefresh.isValid(account, AuthTokenType.REFRESH_TOKEN, now));
        Assertions.assertFalse(tokenForRefresh.isValid(account, AuthTokenType.EMAIL_VERIFICATION, now));
    }

}

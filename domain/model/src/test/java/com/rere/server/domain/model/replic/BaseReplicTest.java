package com.rere.server.domain.model.replic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

/**
 * Contains tests for the {@link BaseReplic} class.
 */
class BaseReplicTest {

    private static final URL URL;

    static {
        try {
            URL = URI.create("https://google.com/").toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static BaseReplic createBaseReplic(String passwordHash, Instant expirationTimestamp) {
        return new BaseReplic(UUID.randomUUID(), Instant.now(), URL, MediaMode.ALL, null, expirationTimestamp, passwordHash, null);
    }

    @Test
    void requiresPasswordWorks() {
        BaseReplic withPassword = createBaseReplic("password", Instant.now());
        BaseReplic withoutPassword = createBaseReplic(null, Instant.now());

        Assertions.assertTrue(withPassword.requiresPassword());
        Assertions.assertFalse(withoutPassword.requiresPassword());
    }

    @Test
    void isExpiredWorks() {
        Instant now = Instant.ofEpochSecond(68493730);
        BaseReplic expiredReplic = createBaseReplic(null, now.minusSeconds(1000));
        BaseReplic nonExpiredReplic1 = createBaseReplic(null, now.plusSeconds(1000));
        BaseReplic nonExpiredReplic2 = createBaseReplic(null, now);

        Assertions.assertTrue(expiredReplic.isExpired(now));
        Assertions.assertFalse(nonExpiredReplic1.isExpired(now));
        Assertions.assertFalse(nonExpiredReplic2.isExpired(now));
    }

}

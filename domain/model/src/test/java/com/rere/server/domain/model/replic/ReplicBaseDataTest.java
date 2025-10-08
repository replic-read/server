package com.rere.server.domain.model.replic;

import com.rere.server.domain.model.impl.ReplicImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

/**
 * Contains tests for the {@link ReplicBaseData} class.
 */
class ReplicBaseDataTest {

    @Test
    void requiresPasswordWorks() {
        ReplicBaseData withPassword = ReplicImpl.builder()
                .passwordHash("password").build();

        ReplicBaseData withoutPassword = ReplicImpl.builder()
                .build();

        Assertions.assertTrue(withPassword.requiresPassword());
        Assertions.assertFalse(withoutPassword.requiresPassword());
    }

    @Test
    void isExpiredWorks() {
        Instant now = Instant.ofEpochSecond(68493730);
        ReplicBaseData expiredReplic = ReplicImpl.builder()
                .expirationTimestamp(now.minusSeconds(1000))
                .build();
        ReplicBaseData nonExpiredReplic1 = ReplicImpl.builder()
                .expirationTimestamp(now.plusSeconds(1000))
                .build();
        ReplicBaseData nonExpiredReplic2 = ReplicImpl.builder()
                .expirationTimestamp(now)
                .build();

        Assertions.assertTrue(expiredReplic.isExpired(now));
        Assertions.assertFalse(nonExpiredReplic1.isExpired(now));
        Assertions.assertFalse(nonExpiredReplic2.isExpired(now));
    }

}

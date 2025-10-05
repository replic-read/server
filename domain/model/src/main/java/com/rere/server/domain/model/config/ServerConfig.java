package com.rere.server.domain.model.config;

import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Models the configuration that can be made by admins on the server.
 */
@Data
public class ServerConfig {

    /**
     * User group that is allowed to create replics.
     */
    @NonNull
    private AuthUserGroup createReplicsGroup;

    /**
     * User group that is allowed to access the content of replics.
     */
    @NonNull
    private AuthUserGroup accessReplicsGroup;

    /**
     * User group that is allowed to report replics.
     */
    @NonNull
    private AuthUserGroup createReportsGroup;

    /**
     * Whether accounts can freely be generated.
     */
    private boolean allowAccountCreation;

    /**
     * The configuration of the replic-limit.
     */
    private ReplicLimitConfig limit;

    /**
     * The maximum timespan between a replics creation and expiration.
     */
    private Period maximumActivePeriod;

    /**
     * Checks whether the current server configuration allows the specific expiration timestamp.
     * @param expirationTimestamp The timestamp that should be checked.
     * @return Whether the timestamp is allowed.
     */
    public boolean allowsExpiration(Instant expirationTimestamp) {
        if(getMaximumActivePeriod() == null) {
            return true;
        }
        ZoneId zone = ZoneId.systemDefault();
        LocalDate now = LocalDate.now(zone);
        LocalDate maximumExpirationDate = now.plus(getMaximumActivePeriod());

        // Shifts the instant to midnight of the next date.
        Instant actualExpirationEndOfDay = expirationTimestamp.plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        LocalDate actualExpirationDate = LocalDate.ofInstant(actualExpirationEndOfDay, zone);

        return actualExpirationDate.isBefore(maximumExpirationDate) ||
               actualExpirationDate.isEqual(maximumExpirationDate);
    }

}

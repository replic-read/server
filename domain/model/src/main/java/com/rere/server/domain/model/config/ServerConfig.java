package com.rere.server.domain.model.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Models the configuration that can be made by admins on the server.
 */
@Data
@AllArgsConstructor
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
     * @param now The reference {@link LocalDate} instance.
     * @param expirationTimestamp The timestamp that should be checked.
     * @return Whether the timestamp is allowed.
     */
    public boolean allowsExpiration(LocalDate now, Instant expirationTimestamp, ZoneId zone) {
        if(getMaximumActivePeriod() == null) {
            return true;
        }

        LocalDate expirationDate = LocalDate.ofInstant(expirationTimestamp, zone);
        LocalDate maximumExpirationDate = now.plus(getMaximumActivePeriod());

        return expirationDate.isBefore(maximumExpirationDate) ||
               expirationDate.isEqual(maximumExpirationDate);
    }

}

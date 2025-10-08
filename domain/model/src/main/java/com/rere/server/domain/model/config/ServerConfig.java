package com.rere.server.domain.model.config;

import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

/**
 * Models the configuration that can be made by admins on the server.
 */
public interface ServerConfig {

    /**
     * User group that is allowed to create replics.
     */
    @NonNull
    AuthUserGroup getCreateReplicsGroup();

    void setCreateReplicsGroup(@NonNull AuthUserGroup createReplicsGroup);

    /**
     * User group that is allowed to access the content of replics.
     */
    @NonNull
    AuthUserGroup getAccessReplicsGroup();

    void setAccessReplicsGroup(@NonNull AuthUserGroup accessReplicsGroup);

    /**
     * User group that is allowed to report replics.
     */
    @NonNull
    AuthUserGroup getCreateReportsGroup();

    void setCreateReportsGroup(@NonNull AuthUserGroup createReportsGroup);

    /**
     * Whether accounts can freely be generated.
     */
    boolean isAllowAccountCreation();

    void setAllowAccountCreation(boolean allowAccountCreation);

    /**
     * The configuration of the replic-limit.
     */
    ReplicLimitConfig getLimit();

    void setLimit(@NonNull ReplicLimitConfig limit);

    /**
     * The maximum timespan between a replics creation and expiration.
     */
    Period getMaximumActivePeriod();

    void setMaximumActivePeriod(@NonNull Period maximumActivePeriod);

    /**
     * Checks whether the current server configuration allows the specific expiration timestamp.
     * @param now The reference {@link LocalDate} instance.
     * @param expirationTimestamp The timestamp that should be checked.
     * @return Whether the timestamp is allowed.
     */
    default boolean allowsExpiration(LocalDate now, Instant expirationTimestamp, ZoneId zone) {
        if(getMaximumActivePeriod() == null) {
            return true;
        }
        if(expirationTimestamp == null) {
            return false;
        }

        LocalDate expirationDate = LocalDate.ofInstant(expirationTimestamp, zone);
        LocalDate maximumExpirationDate = now.plus(getMaximumActivePeriod());

        return expirationDate.isBefore(maximumExpirationDate) ||
               expirationDate.isEqual(maximumExpirationDate);
    }

}

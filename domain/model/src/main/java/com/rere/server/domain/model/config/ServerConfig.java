package com.rere.server.domain.model.config;

import lombok.Data;
import lombok.NonNull;

import java.time.Period;

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

}

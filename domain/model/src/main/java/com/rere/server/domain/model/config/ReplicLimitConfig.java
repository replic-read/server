package com.rere.server.domain.model.config;

import lombok.Data;
import lombok.NonNull;

import java.time.Period;

/**
 * Models the configuration for the replic-limit imposed on a user-basis.
 */
@Data
public class ReplicLimitConfig {

    /**
     * The time period over which a specific amount of replics can be created.
     */
    @NonNull
    private Period period;

    /**
     * The count of replics that can be created
     */
    private int count;

}

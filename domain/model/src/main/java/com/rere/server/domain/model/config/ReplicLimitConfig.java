package com.rere.server.domain.model.config;

import lombok.NonNull;

import java.time.Instant;
import java.time.Period;

/**
 * Models the configuration for the replic-limit imposed on a user-basis.
 */
public interface ReplicLimitConfig {

    /**
     * The time period over which a specific amount of replics can be created.
     */
    @NonNull
    Period getPeriod();

    /**
     * Sets the new period.
     * @param period The new period.
     */
    void setPeriod(@NonNull Period period);

    /**
     * The count of replics that can be created
     */
    int getCount();

    /**
     * Sets the new count.
     * @param count The new count.
     */
    void setCount(int count);

    /**
     * The timestamp on which the current period settings were started.
     */
    @NonNull
    Instant getPeriodStart();

    /**
     * Sets the period start.
     * @param periodStart The period start.
     */
    void setPeriodStart(@NonNull Instant periodStart);

}

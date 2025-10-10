package com.rere.server.domain.service;

import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.exception.ReplicQuotaMetException;

import java.time.Instant;
import java.util.UUID;

/**
 * Service that helps to calculate of data related to the replic quota.
 */
public interface QuotaService {

    /**
     * Gets the timestamp of the beginning of the current period.
     * <br>
     * Note: This is different from the start of the limit period as saved in the settings.
     * This returns the timestamp of the currently running period.
     * @return The timestamp.
     * @throws OperationDisabledException If not limit is setup.
     */
    Instant getCurrentPeriodStart() throws OperationDisabledException;

    /**
     * Gets the number of created replics in the current period of a given account.
     * @param accountId The id of the account.
     * @return The number of created replics.
     * @throws OperationDisabledException If not limit is setup.
     */
    long getCreatedReplicCountInPeriod(UUID accountId) throws OperationDisabledException;

    /**
     * Checks whether an account has reached the full replic quota.
     * @param accountId The id of the account.
     * @throws ReplicQuotaMetException If the quota has been met.
     */
    void checkAccountQuota(UUID accountId) throws ReplicQuotaMetException;

}

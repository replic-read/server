package com.rere.server.domain.service;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.exception.InvalidExpirationException;
import com.rere.server.domain.model.exception.InvalidPasswordException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.ReplicContentWriteException;
import com.rere.server.domain.model.exception.ReplicQuotaMetException;
import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicAccess;
import com.rere.server.domain.model.replic.ReplicState;
import org.springframework.lang.NonNull;

import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service that provides operations on the replics.
 */
public interface ReplicService {

    /**
     * Creates a new replic.
     *
     * @param originalUrl        The url the replic replicates.
     * @param mediaMode          The media mode.
     * @param description        The description.
     * @param expiration         The expiration.
     * @param password           The unencrypted password.
     * @param account            The account that created the replic, or null if anonymous.
     * @param fileWriterCallback The callback to write the contents.
     * @return The created replic.
     * @throws ReplicQuotaMetException     If the account can't create more replics this period.
     * @throws ReplicContentWriteException If the FileWriterCallback failed.
     * @throws InvalidExpirationException  If the expiration was invalid.
     */
    @NonNull
    Replic createReplic(@NonNull URL originalUrl, @NonNull MediaMode mediaMode,
                        String description, Instant expiration, String password, Account account,
                        @NonNull FileWriterCallback fileWriterCallback) throws ReplicQuotaMetException,
            ReplicContentWriteException, InvalidExpirationException;

    /**
     * Gets all replics matching the given filters.
     *
     * @param sort        The comparator to sort the replics.
     * @param replicId    The id of the replic to filter.
     * @param accountId   The id of the account to filter.
     * @param stateFilter The states of the replics to filter.
     * @param query       The query to filter.
     * @return All replics matching the filters above.
     */
    @NonNull
    List<Replic> getReplics(Comparator<Replic> sort, UUID replicId, UUID accountId, Set<ReplicState> stateFilter, String query);

    /**
     * Sets the state of a specific replic.
     *
     * @param replicId The id of the replic.
     * @param state    The new state of the replic.
     * @return The replic whose id was changed.
     * @throws NotFoundException If the replic was not found.
     */
    @NonNull
    Replic setReplicState(@NonNull UUID replicId, @NonNull ReplicState state) throws NotFoundException;

    /**
     * Marks a specific replic as being visited.
     *
     * @param replicId  The id of the replic to visit.
     * @param visitorId The id of the visitor, or null for anonymous.
     * @return The created {@link ReplicAccess}.
     * @throws NotFoundException If the replic or visitor were not found.
     */
    @NonNull
    ReplicAccess visitReplic(@NonNull UUID replicId, UUID visitorId) throws NotFoundException;

    /**
     * Gets the content stream of a specific replic.
     *
     * @param replicId The id of the replic.
     * @param password The password to the replic.
     * @return The stream containing the content.
     * @throws NotFoundException        If the replic was not found.
     * @throws InvalidPasswordException If the password was invalid.
     */
    @NonNull
    InputStream receiveContent(@NonNull UUID replicId, String password) throws NotFoundException, InvalidPasswordException;

}

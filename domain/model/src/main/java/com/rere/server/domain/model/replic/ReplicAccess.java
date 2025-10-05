package com.rere.server.domain.model.replic;

import com.rere.server.domain.model.account.Account;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Models an access to a replic.
 *
 * @param id The id of the access
 * @param creationTimestamp The creation timestamp of the access.
 * @param replic The replic that was accesses.
 * @param visitor The account that visited the replic, if the access wasn't anonymous.
 */
public record ReplicAccess(@NonNull UUID id, @NonNull Instant creationTimestamp, @NonNull Replic replic,
                           Account visitor) {
}

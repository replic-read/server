package com.rere.server.inter.authorization;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicState;
import jakarta.annotation.Nonnull;

/**
 * Interface that performs decisions on whether a client can execute an operation.
 * Decision answers are communicated via throwing of the {@link AuthorizationException}.
 * <br>
 * All methods that accept an account can also accept null, which denotes that the client is not authenticated.
 */
public interface Authorizer {

    /**
     * Requires permission to access replic content.
     */
    void requireAccessReplics(Account account);

    /**
     * Requires permission to create replics.
     */
    void requireCreateReplics(Account account);

    /**
     * Requires permission to create accounts.
     */
    void requireCreateAccount(Account account);

    /**
     * Requires permission to access the full data of accounts.
     */
    void requireAccessAccountsFull(Account account);

    /**
     * Requires permission to change the server config.
     * Also includes access to 'shutdown' and 'restart'.
     */
    void requireChangeServerConfig(Account account);

    /**
     * Requires permission to review reports.
     */
    void requireReviewReports(Account account);

    /**
     * Requires permission to access reports.
     */
    void requireAccessReports(Account account);

    /**
     * Requires permission to create reports.
     */
    void requireCreateReports(Account account);

    /**
     * Requires permission to review reports.
     *
     * @param replic The replic whose state should be updated.
     * @param  state The new state of the replic.
     */
    void requireUpdateReplicState(Account account, @Nonnull Replic replic, @Nonnull ReplicState state);

}

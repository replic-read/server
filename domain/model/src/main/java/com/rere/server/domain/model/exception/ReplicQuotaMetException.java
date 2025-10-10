package com.rere.server.domain.model.exception;

import com.rere.server.domain.model.account.Account;
import lombok.Getter;

import java.util.UUID;

/**
 * Thrown when an action cannot be performed because the replic quota of an account was met.
 */
@Getter
public class ReplicQuotaMetException extends DomainException {

    private static final String MESSAGE_FORMAT = "Replic quota overstepped by account with id '%s'.";

    /**
     * The account that tried to overstep ther quota.
     */
    private final transient UUID accountId;

    /**
     * Creates a new DomainException with a given message.
     *
     * @param account The account.
     */
    public ReplicQuotaMetException(Account account) {
        super(MESSAGE_FORMAT.formatted(account.getId().toString()));
        this.accountId = account.getId();
    }

    /**
     * Creates a new DomainException with a given message.
     *
     * @param accountId The id of the account.
     */
    public ReplicQuotaMetException(UUID accountId) {
        super(MESSAGE_FORMAT.formatted(accountId.toString()));
        this.accountId = accountId;
    }
}

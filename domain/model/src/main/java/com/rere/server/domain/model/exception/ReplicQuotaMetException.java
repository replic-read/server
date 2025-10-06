package com.rere.server.domain.model.exception;

import com.rere.server.domain.model.account.Account;
import lombok.Getter;

/**
 * Thrown when an action cannot be performed because the replic quota of an account was met.
 */
@Getter
public class ReplicQuotaMetException extends DomainException {

    private static final String MESSAGE_FORMAT = "Replic quota overstepped by account '%s'.";

    /**
     * The account that tried to overstep ther quota.
     */
    private final transient Account account;

    /**
     * Creates a new DomainException with a given message.
     *
     * @param account The account.
     */
    public ReplicQuotaMetException(Account account) {
        super(MESSAGE_FORMAT.formatted(account.getId().toString()));
        this.account = account;
    }
}

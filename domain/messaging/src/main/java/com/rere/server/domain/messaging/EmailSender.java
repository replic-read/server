package com.rere.server.domain.messaging;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AuthToken;

/**
 * Provides methods to send various emails to accounts.
 */
public interface EmailSender {

    /**
     * Sends an email-verification message to the user.
     * @param account The account to send the message to.
     * @param token The token to include.
     * @param htmlEmail Whether to mark up the email.
     * @return Whether the email was sent successfully.
     */
    boolean sendVerificationToken(Account account, AuthToken token, boolean htmlEmail);

}

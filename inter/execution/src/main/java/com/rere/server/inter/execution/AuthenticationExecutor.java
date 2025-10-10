package com.rere.server.inter.execution;

import com.rere.server.inter.dto.request.CreateAccountRequest;
import com.rere.server.inter.dto.request.CredentialsRequest;
import com.rere.server.inter.dto.request.RefreshRequest;
import com.rere.server.inter.dto.request.SubmitEmailVerificationRequest;
import com.rere.server.inter.dto.response.AccountWithTokensReponse;

/**
 * Executor for authentication matters.
 */
public interface AuthenticationExecutor {

    /**
     * Executor for POST /auth/submit-email-verification/.
     * @param request The request body.
     */
    void submitEmailVerification(SubmitEmailVerificationRequest request);

    /**
     * Executor for POST /auth/signup/.
     * @param request The request body.
     * @param sendEmail The 'send_email' query parameter.
     */
    AccountWithTokensReponse signup(CreateAccountRequest request, boolean sendEmail);

    /**
     * Executor for POST /auth/refresh/.
     * @param request The request body.
     * @return The response body.
     */
    AccountWithTokensReponse refresh(RefreshRequest request);

    /**
     * Executor for POST /auth/login/.
     * @param request The request body.
     * @return The response body.
     */
    AccountWithTokensReponse login(CredentialsRequest request);

    /**
     * Executor for GET /auth/request-email-verification/.
     * @param html The 'html_email' query parameter.
     */
    void requestEmailVerification(boolean html);

}

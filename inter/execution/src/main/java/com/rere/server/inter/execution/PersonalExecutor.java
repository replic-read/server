package com.rere.server.inter.execution;

import com.rere.server.inter.dto.request.UpdateAccountRequest;
import com.rere.server.inter.dto.response.AccountResponse;
import com.rere.server.inter.dto.response.QuotaProgressResponse;

/**
 * Request executor for requests for the account of the client.
 */
public interface PersonalExecutor {

    /**
     * Executor for GET /me/.
     * @return The response body.
     */
    AccountResponse getMe();

    /**
     * Executor for POST /me/.
     * @param request The request body.
     * @return The response body.
     */
    AccountResponse updateMe(UpdateAccountRequest request);

    /**
     * Executor for GET /me/quota/.
     * @return The response body.
     */
    QuotaProgressResponse getQuotaProgress();

}

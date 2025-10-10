package com.rere.server.inter.execution;

import com.rere.server.inter.execution.dto.request.CreateAccountRequest;
import com.rere.server.inter.execution.dto.response.AccountResponse;
import com.rere.server.inter.execution.dto.response.PartialAccountResponse;

import java.util.List;
import java.util.Set;

/**
 * The executor dealing with account-related requests.
 */
public interface AccountExecutor<ASP, AS, D, I> {

    /**
     * Executor for POST /accounts/.
     * @param request The request body.
     * @param sendVerificationEmail The 'send_email' query parameter.
     * @param verified The 'verified' query parameter.
     * @return The response body.
     */
    AccountResponse createAccount(CreateAccountRequest request, boolean sendVerificationEmail, boolean verified);

    /**
     * Executor for GET /accounts/partial/.
     * @param sort The 'sort' query parameter.
     * @param direction The 'direction' query parameter.
     * @param accountId The 'account_id' query parameter.
     * @param query The 'query' parameter.
     * @return The response body.
     */
    List<PartialAccountResponse> getAccountsPartial(ASP sort, D direction, I accountId, String query);

    /**
     * Executor for GET /accounts/full/.
     * @param sort The 'sort' query parameter.
     * @param direction The 'direction' query parameter.
     * @param accountId The 'account_id' query parameter.
     * @param filter The 'filter' query parameter.
     * @param query The 'query' parameter.
     * @return The response body.
     */
    List<AccountResponse> getAccountsFull(ASP sort, D direction, I accountId, Set<AS> filter, String query);

}

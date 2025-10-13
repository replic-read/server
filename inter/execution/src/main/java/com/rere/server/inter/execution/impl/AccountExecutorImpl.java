package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.AuthenticationService;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ReportService;
import com.rere.server.domain.service.ServerConfigService;
import com.rere.server.inter.authorization.Authorizer;
import com.rere.server.inter.dto.parameter.AccountSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.dto.request.CreateAccountRequest;
import com.rere.server.inter.dto.request.ResetPasswordRequest;
import com.rere.server.inter.dto.response.AccountResponse;
import com.rere.server.inter.dto.response.PartialAccountResponse;
import com.rere.server.inter.execution.AbstractExecutor;
import com.rere.server.inter.execution.AccountExecutor;
import com.rere.server.inter.execution.HttpErrorResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Primary
@Component
public class AccountExecutorImpl extends AbstractExecutor implements AccountExecutor<AccountSortParameter, AccountState, SortDirectionParameter, UUID> {
    @Autowired
    protected AccountExecutorImpl(AccountService accountService, AuthenticationService authService, ReplicService replicService, ReportService reportService, ServerConfigService configService, QuotaService quotaService, Authorizer authorizer) {
        super(accountService, authService, replicService, reportService, configService, quotaService, authorizer);
    }

    @Override
    public AccountResponse createAccount(CreateAccountRequest request, boolean sendVerificationEmail, boolean verified) {
        authorizer.requireChangeServerConfig(getAuth());

        try {
            Account created = authService.createAccount(request.email(), request.username(), request.password(),
                    request.profileColor(), false, verified, sendVerificationEmail, true);
            return createAccountResponse(created);
        } catch (NotUniqueException e) {
            throw HttpErrorResponseException.fromDomainException(e);
        } catch (OperationDisabledException e) {
            // OperationDisabledException: We bypass config rules.
            throw new IllegalStateException(e);
        }
    }

    @Override
    public AccountResponse resetAccountPassword(ResetPasswordRequest request, UUID accountId) {
        authorizer.requireChangeServerConfig(getAuth());

        Account account;

        try {
            authService.changePassword(accountId, request.password());
            account = accountService.getAccountById(accountId)
                    .orElseThrow(() -> NotFoundException.account(accountId));
        } catch (NotFoundException e) {
            throw HttpErrorResponseException.fromDomainException(e);
        }

        return createAccountResponse(account);
    }

    @Override
    public List<PartialAccountResponse> getAccountsPartial(AccountSortParameter sort, SortDirectionParameter direction, UUID accountId, String query) {
        return accountService.getAccounts(getComparatorFromSort(sort, direction), accountId, null, query)
                .stream().map(this::createAccountResponsePartial)
                .toList();
    }

    @Override
    public List<AccountResponse> getAccountsFull(AccountSortParameter sort, SortDirectionParameter direction, UUID accountId, Set<AccountState> filter, String query) {
        authorizer.requireAccessAccountsFull(getAuth());
        return accountService.getAccounts(getComparatorFromSort(sort, direction), accountId, filter, query)
                .stream().map(this::createAccountResponse)
                .toList();
    }
}

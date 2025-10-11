package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.account.Account;
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
import com.rere.server.inter.dto.request.UpdateAccountRequest;
import com.rere.server.inter.dto.response.AccountResponse;
import com.rere.server.inter.dto.response.QuotaProgressResponse;
import com.rere.server.inter.execution.AbstractExecutor;
import com.rere.server.inter.execution.HttpErrorResponseException;
import com.rere.server.inter.execution.PersonalExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonalExecutorImpl extends AbstractExecutor implements PersonalExecutor {
    @Autowired
    protected PersonalExecutorImpl(AccountService accountService, AuthenticationService authService, ReplicService replicService, ReportService reportService, ServerConfigService configService, QuotaService quotaService, Authorizer authorizer) {
        super(accountService, authService, replicService, reportService, configService, quotaService, authorizer);
    }

    @Override
    public AccountResponse getMe() {
        return createAccountResponse(getAuth());
    }

    @Override
    public AccountResponse updateMe(UpdateAccountRequest updateRequest) {
        Account updated;
        try {
            updated = accountService.updateAccount(getAuth().getId(), updateRequest.email(), updateRequest.username(), updateRequest.profileColor());
        } catch (NotUniqueException e) {
            throw HttpErrorResponseException.fromDomainException(e);
        } catch (NotFoundException e) {
            throw new IllegalStateException(e);
        }

        return createAccountResponse(updated);
    }

    @Override
    public QuotaProgressResponse getQuotaProgress() {
        try {
            long count = quotaService.getCreatedReplicCountInPeriod(getAuth().getId());
            return new QuotaProgressResponse(count);
        } catch (OperationDisabledException e) {
            throw HttpErrorResponseException.fromDomainException(e);
        }
    }

}

package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.exception.InvalidTokenException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.AuthenticationService;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ReportService;
import com.rere.server.domain.service.ServerConfigService;
import com.rere.server.inter.authorization.AuthorizationException;
import com.rere.server.inter.authorization.Authorizer;
import com.rere.server.inter.dto.request.CreateAccountRequest;
import com.rere.server.inter.dto.request.CredentialsRequest;
import com.rere.server.inter.dto.request.RefreshRequest;
import com.rere.server.inter.dto.request.SubmitEmailVerificationRequest;
import com.rere.server.inter.dto.response.AccountWithTokensResponse;
import com.rere.server.inter.execution.AbstractExecutor;
import com.rere.server.inter.execution.AuthenticationExecutor;
import com.rere.server.inter.execution.HttpErrorResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthenticationExecutorImpl extends AbstractExecutor implements AuthenticationExecutor {
    @Autowired
    protected AuthenticationExecutorImpl(AccountService accountService, AuthenticationService authService, ReplicService replicService, ReportService reportService, ServerConfigService configService, QuotaService quotaService, Authorizer authorizer) {
        super(accountService, authService, replicService, reportService, configService, quotaService, authorizer);
    }

    @Override
    public void submitEmailVerification(SubmitEmailVerificationRequest request) {
        try {
            authService.validateEmail(UUID.fromString(request.emailToken()));
        } catch (InvalidTokenException e) {
            throw HttpErrorResponseException.fromDomainException(e);
        }
    }

    @Override
    public AccountWithTokensResponse signup(CreateAccountRequest request, boolean sendEmail) {
        authorizer.requireCreateAccount(getAuth());
        try {
            Account account = authService.createAccount(request.email(), request.username(), request.password(),
                    request.profileColor(), false, false, sendEmail, false);
            UUID refreshToken = authService.createRefreshToken(account.getId());
            String accessToken = authService.createAccessToken(account.getId());

            return new AccountWithTokensResponse(
                    createAccountResponse(account),
                    accessToken,
                    refreshToken.toString()
            );
        } catch (NotUniqueException | OperationDisabledException e) {
            throw HttpErrorResponseException.fromDomainException(e);
        } catch (NotFoundException e) {
            // The user id is definitely valid.
            throw new IllegalStateException(e);
        }
    }

    @Override
    public AccountWithTokensResponse refresh(RefreshRequest request) {
        Optional<Account> account = authService.authenticateWithRefreshToken(UUID.fromString(request.refreshToken()));
        if (account.isPresent()) {
            return loginUser(account.get(), request.refreshToken());
        }

        // Terrible, but easiest way to create a consistent error response for a bad token.
        try {
            throw new InvalidTokenException();
        } catch (InvalidTokenException e) {
            throw HttpErrorResponseException.fromDomainException(e);
        }
    }

    private AccountWithTokensResponse loginUser(Account account, String oldRefreshToken) {
        String accessToken;
        try {
            String refreshToken = oldRefreshToken != null ? oldRefreshToken : authService.createRefreshToken(account.getId()).toString();
            accessToken = authService.createAccessToken(account.getId());
            return new AccountWithTokensResponse(
                    createAccountResponse(account),
                    accessToken,
                    refreshToken
            );
        } catch (NotFoundException e) {
            // Never thrown because account id is valid.
            throw new IllegalStateException(e);
        }
    }

    @Override
    public AccountWithTokensResponse login(CredentialsRequest request) {
        Optional<Account> account = authService.authenticateWithCredentials(request.email(), request.username(), request.password());

        if (account.isPresent()) {
            return loginUser(account.get(), null);
        }

        throw AuthorizationException.genericUnauthorized();
    }

    @Override
    public void requestEmailVerification(boolean html) {
        try {
            authService.requestEmailVerification(getAuth().getId(), html);
        } catch (NotFoundException e) {
            // The account id is valid.
            throw new IllegalStateException(e);
        }
    }
}

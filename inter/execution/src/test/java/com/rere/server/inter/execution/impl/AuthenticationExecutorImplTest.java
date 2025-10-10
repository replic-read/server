package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.InvalidTokenException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.NotUniqueSubject;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.inter.authorization.AuthorizationException;
import com.rere.server.inter.execution.dto.request.CreateAccountRequest;
import com.rere.server.inter.execution.dto.request.CredentialsRequest;
import com.rere.server.inter.execution.dto.request.RefreshRequest;
import com.rere.server.inter.execution.dto.request.SubmitEmailVerificationRequest;
import com.rere.server.inter.execution.dto.response.AccountWithTokensReponse;
import com.rere.server.inter.execution.error.HttpErrorResponseException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationExecutorImplTest extends BaseExecutorTest {

    @InjectMocks
    private AuthenticationExecutorImpl subject;

    @Test
    void submitEmailVerificationConvertsInvalidToken() throws DomainException {
        doThrow(new InvalidTokenException()).when(authService).validateEmail(any());

        assertThrows(HttpErrorResponseException.class,
                () -> subject.submitEmailVerification(new SubmitEmailVerificationRequest(UUID.randomUUID().toString())));
    }

    @Test
    void submitEmailVerificationCallsAuthService() throws DomainException {
        subject.submitEmailVerification(new SubmitEmailVerificationRequest(UUID.randomUUID().toString()));

        verify(authService, times(1)).validateEmail(any());
    }

    @Test
    void signupCreatesAccountAndTokens() throws DomainException {
        when(authService.createAccount(any(), any(), any(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(AccountImpl.builder().build());
        UUID refresh = UUID.randomUUID();
        when(authService.createRefreshToken(any())).thenReturn(refresh);
        when(authService.createAccessToken(any())).thenReturn("access-token");

        AccountWithTokensReponse response = subject.signup(new CreateAccountRequest("em", "pa", 0, "us"), true);

        assertEquals(refresh.toString(), response.refreshToken());
        assertEquals("access-token", response.accessToken());
    }

    @Test
    void signupConvertsNotUnique() throws DomainException {
        when(authService.createAccount(any(), any(), any(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenThrow(new NotUniqueException(NotUniqueSubject.EMAIL));

        assertThrows(HttpErrorResponseException.class,
                () -> subject.signup(new CreateAccountRequest("em", "pa", 0, "us"), true));
    }

    @Test
    void signupConvertsNotFound() throws DomainException {
        when(authService.createAccount(any(), any(), any(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(AccountImpl.builder().build());
        when(authService.createRefreshToken(any())).thenThrow(NotFoundException.account(UUID.randomUUID()));

        assertThrows(IllegalStateException.class,
                () -> subject.signup(new CreateAccountRequest("em", "pa", 0, "us"), true));
    }

    @Test
    void refreshCreatesAccessUSesOldRefresh() throws DomainException {
        Account account = AccountImpl.builder().build();
        UUID refreshToken = UUID.randomUUID();

        when(authService.authenticateWithRefreshToken(any())).thenReturn(Optional.of(account));
        when(authService.createAccessToken(any())).thenReturn("access-token");

        AccountWithTokensReponse response = subject.refresh(new RefreshRequest(refreshToken.toString()));

        assertEquals(refreshToken.toString(), response.refreshToken());
        assertEquals("access-token", response.accessToken());
        assertEquals(account.getId().toString(), response.account().id());
    }

    @Test
    void refreshConvertsNotFound() throws DomainException {
        Account account = AccountImpl.builder().build();
        UUID refreshToken = UUID.randomUUID();

        when(authService.authenticateWithRefreshToken(any())).thenReturn(Optional.of(account));
        when(authService.createAccessToken(any())).thenThrow(NotFoundException.account(account.getId()));

        assertThrows(IllegalStateException.class,
                () -> subject.refresh(new RefreshRequest(refreshToken.toString())));
    }

    @Test
    void refreshThrowsWhenAccNotFound() {
        when(authService.authenticateWithRefreshToken(any())).thenReturn(Optional.empty());

        assertThrows(HttpErrorResponseException.class,
                () -> subject.refresh(new RefreshRequest(UUID.randomUUID().toString())));
    }

    @Test
    void loginUsesNewRefreshToken() throws DomainException {
        Account account = AccountImpl.builder().build();
        UUID refreshToken = UUID.randomUUID();

        when(authService.authenticateWithCredentials(any(), any(), any())).thenReturn(Optional.of(account));
        when(authService.createAccessToken(any())).thenReturn("access-token");
        when(authService.createRefreshToken(account.getId())).thenReturn(refreshToken);

        AccountWithTokensReponse response = subject.login(new CredentialsRequest("em", "us", "pa"));

        verify(authService, times(1)).createRefreshToken(account.getId());

        assertEquals(refreshToken.toString(), response.refreshToken());
        assertEquals("access-token", response.accessToken());
        assertEquals(account.getId().toString(), response.account().id());
    }

    @Test
    void loginThrowsWhenAccNotFound() {
        when(authService.authenticateWithCredentials(any(), any(), any())).thenReturn(Optional.empty());

        assertThrows(AuthorizationException.class,
                () -> subject.login(new CredentialsRequest("em", "us", "pa")));
    }

    @Test
    void requestEmailVerificationCallsService() throws DomainException {
        subject.setAuth(AccountImpl.builder().build());
        subject.requestEmailVerification(true);

        verify(authService, times(1)).requestEmailVerification(any(), anyBoolean());
    }

    @Test
    void requestEmailVerificationConvertsNotFound() throws DomainException {
        subject.setAuth(AccountImpl.builder().build());
        doThrow(NotFoundException.account(UUID.randomUUID()))
                .when(authService).requestEmailVerification(any(), anyBoolean());

        assertThrows(IllegalStateException.class,
                () -> subject.requestEmailVerification(true));
    }

}
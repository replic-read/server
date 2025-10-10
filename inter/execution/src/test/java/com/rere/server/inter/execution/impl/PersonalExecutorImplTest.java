package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.NotUniqueSubject;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.exception.OperationDisabledOperation;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.inter.execution.dto.request.UpdateAccountRequest;
import com.rere.server.inter.execution.dto.response.AccountResponse;
import com.rere.server.inter.execution.dto.response.QuotaProgressResponse;
import com.rere.server.inter.execution.error.HttpErrorResponseException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonalExecutorImplTest extends BaseExecutorTest {

    @InjectMocks
    private PersonalExecutorImpl subject;

    @Test
    void getMeReturnsAuth() {
        Account account = AccountImpl.builder().build();
        subject.setAuth(account);

        AccountResponse response = subject.getMe();

        assertEquals(account.getId().toString(), response.id());
        assertEquals(account.getCreationTimestamp().toString(), response.createdTimestamp());
    }

    @Test
    void updateMeCallsService() throws DomainException {
        Account account = AccountImpl.builder().build();
        subject.setAuth(account);

        when(accountService.updateAccount(any(), any(), any(), anyInt()))
                .thenReturn(account);

        AccountResponse response = subject.updateMe(new UpdateAccountRequest("em", "us", 4));

        verify(accountService, times(1)).updateAccount(eq(account.getId()), any(), any(), anyInt());

        assertEquals(account.getId().toString(), response.id());
    }

    @Test
    void updateMeConvertsNotUnique() throws DomainException {
        subject.setAuth(AccountImpl.builder().build());

        when(accountService.updateAccount(any(), any(), any(), anyInt()))
                .thenThrow(new NotUniqueException(NotUniqueSubject.EMAIL));

        assertThrows(HttpErrorResponseException.class,
                () -> subject.updateMe(new UpdateAccountRequest("em", "us", 4)));
    }

    @Test
    void updateMeConvertsNotFound() throws DomainException {
        subject.setAuth(AccountImpl.builder().build());

        when(accountService.updateAccount(any(), any(), any(), anyInt()))
                .thenThrow(NotFoundException.account(UUID.randomUUID()));

        assertThrows(IllegalStateException.class,
                () -> subject.updateMe(new UpdateAccountRequest("em", "us", 4)));
    }

    @Test
    void getQuotaProgressReturnsQuota() throws DomainException {
        subject.setAuth(AccountImpl.builder().build());

        when(quotaService.getCreatedReplicCountInPeriod(any()))
                .thenReturn(42L);

        QuotaProgressResponse quota = subject.getQuotaProgress();

        assertEquals(42, quota.count());
    }

    @Test
    void getQuotaProgressConvertsDisabled() throws DomainException {
        subject.setAuth(AccountImpl.builder().build());

        when(quotaService.getCreatedReplicCountInPeriod(any()))
                .thenThrow(new OperationDisabledException(OperationDisabledOperation.QUOTA_INFO));

        assertThrows(HttpErrorResponseException.class,
                () -> subject.getQuotaProgress());
    }

}
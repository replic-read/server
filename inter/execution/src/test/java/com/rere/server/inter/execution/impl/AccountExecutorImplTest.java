package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.NotUniqueSubject;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.exception.OperationDisabledOperation;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.inter.execution.dto.request.CreateAccountRequest;
import com.rere.server.inter.execution.dto.response.AccountResponse;
import com.rere.server.inter.execution.dto.response.PartialAccountResponse;
import com.rere.server.inter.execution.error.HttpErrorResponseException;
import com.rere.server.inter.execution.parameter.AccountSortParameter;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link AccountExecutorImpl} class.
 */
class AccountExecutorImplTest extends BaseExecutorTest {

    private final List<Account> accountList = IntStream.range(0, 10)
            .mapToObj(i -> (Account) AccountImpl.builder().build())
            .toList();

    @InjectMocks
    private AccountExecutorImpl subject;

    @Test
    void createAccountPropagatesAuthorization() {
        assertAuthorizationIsPropagated(() -> subject.createAccount(null, false, false));
    }

    @Test
    void createAccountCallsAuthService() throws DomainException {
        when(authService.createAccount(any(), any(), any(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(AccountImpl.builder().build());

        subject.createAccount(new CreateAccountRequest("", "", 0, ""), false, false);

        verify(authService, times(1)).createAccount("", "", "", 0, false, false, false, true);
    }

    @Test
    void createAccountConvertsNotUnique() throws DomainException {
        when(authService.createAccount(any(), any(), any(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenThrow(new NotUniqueException(NotUniqueSubject.EMAIL));

        assertThrows(HttpErrorResponseException.class,
                () -> subject.createAccount(new CreateAccountRequest("", "", 0, ""), false, false));
    }

    @Test
    void createAccountConvertsNotFound() throws DomainException {
        when(authService.createAccount(any(), any(), any(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenThrow(new OperationDisabledException(OperationDisabledOperation.SIGNUP));
        assertThrows(IllegalStateException.class,
                () -> subject.createAccount(new CreateAccountRequest("", "", 0, ""), false, false));
    }

    @Test
    void getAccountsPartialDelegatesToService() {
        when(accountService.getAccounts(any(), any(), any(), any())).thenReturn(accountList);

        List<PartialAccountResponse> response = subject.getAccountsPartial(AccountSortParameter.CREATION, null, null, null);

        verify(accountService, times(1)).getAccounts(any(), any(), any(), any());
        assertEquals(10, response.size());
    }

    @Test
    void getAccountsFullDelegatesToService() {
        when(accountService.getAccounts(any(), any(), any(), any())).thenReturn(accountList);

        List<AccountResponse> response = subject.getAccountsFull(null, null, null, null, null);

        verify(accountService, times(1)).getAccounts(any(), any(), any(), any());
        assertEquals(10, response.size());
    }

}

package com.rere.server.inter.dispatching.controller;

import com.rere.server.domain.model.account.AccountState;
import com.rere.server.inter.dto.parameter.AccountSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.dto.response.AccountResponse;
import com.rere.server.inter.dto.response.PartialAccountResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contains tests for the {@link AccountController} class.
 */
class AccountControllerTest extends AbstractControllerTest {

    @Test
    void createAccountCallsExecutorAndReturns() throws Exception {
        String content = """
                {
                "email": "user@example.com",
                "username": "user123",
                "profile_color": 5,
                "password": "passwor§"
                }
                """;

        UUID accountId = UUID.randomUUID();
        when(accountExecutor.createAccount(any(), anyBoolean(), anyBoolean())).thenReturn(
                new AccountResponse(accountId.toString(), Instant.now().toString(), "email", "username", 42, "active")
        );

        client.perform(post("/accounts/")
                        .content(content)
                        .queryParam("send_email", "true")
                        .queryParam("verified", "true")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.profile_color").value(42));

        var emailCaptor = ArgumentCaptor.<Boolean>captor();
        var verifiedCaptor = ArgumentCaptor.<Boolean>captor();

        verify(accountExecutor).createAccount(any(), emailCaptor.capture(), verifiedCaptor.capture());

        assertTrue(emailCaptor.getValue());
        assertTrue(verifiedCaptor.getValue());
    }

    @Test
    void getAccountsPartialCallsExecutorAndReturns() throws Exception {
        when(accountExecutor.getAccountsPartial(any(), any(), any(), any())).thenReturn(
                List.of(new PartialAccountResponse("username1", 32, "active"),
                        new PartialAccountResponse("username3", 37, "unverified"))
        );

        UUID accId = UUID.randomUUID();
        client.perform(get("/accounts/partial/")
                        .queryParam("sort", "status")
                        .queryParam("direction", "ascending")
                        .queryParam("account_id", accId.toString())
                        .queryParam("query", "<query>")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].profile_color").value(32));

        var sortCaptor = ArgumentCaptor.<AccountSortParameter>captor();
        var directionCaptor = ArgumentCaptor.<SortDirectionParameter>captor();
        var accIdCaptor = ArgumentCaptor.<UUID>captor();
        var queryCaptor = ArgumentCaptor.<String>captor();

        verify(accountExecutor).getAccountsPartial(sortCaptor.capture(), directionCaptor.capture(), accIdCaptor.capture(), queryCaptor.capture());

        assertEquals(AccountSortParameter.STATUS, sortCaptor.getValue());
        assertEquals(SortDirectionParameter.ASCENDING, directionCaptor.getValue());
        assertEquals(accId, accIdCaptor.getValue());
        assertEquals("<query>", queryCaptor.getValue());
    }

    @Test
    void getAccountsFullCallsExecutorAndReturns() throws Exception {
        UUID accId = UUID.randomUUID();
        when(accountExecutor.getAccountsFull(any(), any(), any(), any(), any())).thenReturn(
                List.of(new AccountResponse(accId.toString(), Instant.now().toString(), "email@server.com", "user123", 33, "inactive"),
                        new AccountResponse(accId.toString(), Instant.now().toString(), "email@server1.com", "user124", 39, "inactive"))
        );

        client.perform(get("/accounts/full/")
                        .queryParam("sort", "status")
                        .queryParam("direction", "ascending")
                        .queryParam("account_id", accId.toString())
                        .queryParam("query", "<query>")
                        .queryParam("filter", "active", "unverified")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].profile_color").value(33))
                .andExpect(jsonPath("$[0].email").value("email@server.com"))
                .andExpect(jsonPath("$[1].email").value("email@server1.com"));

        var sortCaptor = ArgumentCaptor.<AccountSortParameter>captor();
        var directionCaptor = ArgumentCaptor.<SortDirectionParameter>captor();
        var accIdCaptor = ArgumentCaptor.<UUID>captor();
        var queryCaptor = ArgumentCaptor.<String>captor();
        var filterCaptor = ArgumentCaptor.<Set<AccountState>>captor();

        verify(accountExecutor).getAccountsFull(sortCaptor.capture(), directionCaptor.capture(), accIdCaptor.capture(), filterCaptor.capture(), queryCaptor.capture());

        assertEquals(AccountSortParameter.STATUS, sortCaptor.getValue());
        assertEquals(SortDirectionParameter.ASCENDING, directionCaptor.getValue());
        assertEquals(accId, accIdCaptor.getValue());
        assertEquals("<query>", queryCaptor.getValue());
        assertEquals(Set.of(AccountState.ACTIVE, AccountState.UNVERIFIED), filterCaptor.getValue());
    }

}
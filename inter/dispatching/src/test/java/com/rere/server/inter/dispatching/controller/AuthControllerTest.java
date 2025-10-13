package com.rere.server.inter.dispatching.controller;

import com.rere.server.inter.dto.request.SubmitEmailVerificationRequest;
import com.rere.server.inter.dto.response.AccountResponse;
import com.rere.server.inter.dto.response.AccountWithTokensResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contains tests for the {@link AuthControllerTest} class.
 */
class AuthControllerTest extends AbstractControllerTest {

    @Test
    void submitEmailVerificationCallsExecutorAndReturns() throws Exception {
        UUID emailToken = UUID.randomUUID();
        String content = """
                {
                "email_token": "%s"
                }
                """.formatted(emailToken.toString());

        client.perform(post("/auth/submit-email-verification/").content(content))
                .andExpect(status().isOk());

        verify(authExecutor).submitEmailVerification(new SubmitEmailVerificationRequest(emailToken.toString()));
    }

    @Test
    void signupCallsExecutorAndReturns() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(authExecutor.signup(any(), anyBoolean()))
                .thenReturn(new AccountWithTokensResponse(
                                new AccountResponse(accountId.toString(), Instant.now().toString(),
                                        "user@example.com", "user123", 42, "active"),
                                "access-token",
                                "refresh-token"
                        )
                );
        String content = """
                {
                  "email": "user@example.com",
                  "username": "user123",
                  "password": "secret$123"
                }
                """;

        client.perform(post("/auth/signup/").content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.id").value(accountId.toString()))
                .andExpect(jsonPath("$.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.access_token").value("access-token"));
    }

    @Test
    void refreshCallsExecutorAndReturns() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID refreshToken = UUID.randomUUID();
        when(authExecutor.refresh(any()))
                .thenReturn(new AccountWithTokensResponse(
                                new AccountResponse(accountId.toString(), Instant.now().toString(),
                                        "user@example.com", "user123", 42, "active"),
                                "access-token",
                                "refresh-token"
                        )
                );
        String content = """
                {
                  "refresh_token": "%s"
                }
                """.formatted(refreshToken.toString());

        client.perform(post("/auth/refresh/").content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.id").value(accountId.toString()))
                .andExpect(jsonPath("$.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.access_token").value("access-token"));
    }

    @Test
    void loginCallsExecutorAndReturns() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(authExecutor.login(any()))
                .thenReturn(new AccountWithTokensResponse(
                                new AccountResponse(accountId.toString(), Instant.now().toString(),
                                        "user@example.com", "user123", 42, "active"),
                                "access-token",
                                "refresh-token"
                        )
                );
        String content = """
                {
                  "email": "user@gmail.com",
                  "username": "user123",
                  "password": "secret$123"
                }
                """;

        client.perform(post("/auth/login/").content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.id").value(accountId.toString()))
                .andExpect(jsonPath("$.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.access_token").value("access-token"));
    }

    @Test
    void requestEmailVerificationFailsForNoAuth() throws Exception {
        assertForbidden(get("/auth/request-email-verification/"));
    }

    @Test
    void requestEmailVerificationCallsExecutorAndReturns() throws Exception {
        setupAuth();
        client.perform(get("/auth/request-email-verification/").queryParam("html", "false"))
                .andExpect(status().isOk());

        ArgumentCaptor<Boolean> htmlCaptor = ArgumentCaptor.captor();

        verify(authExecutor).requestEmailVerification(htmlCaptor.capture());

        assertFalse(htmlCaptor.getValue());
    }

    @Test
    void logoutFailsForNoAuth() throws Exception {
        assertForbidden(post("/auth/logout/"));
    }

    @Test
    void logoutCallsExecutorAndReturns() throws Exception {
        UUID id = UUID.randomUUID();
        setupAuth();
        client.perform(post("/auth/logout/")
                        .queryParam("token", id.toString()))
                .andExpect(status().isOk());

        var idCaptor = ArgumentCaptor.<UUID>captor();
        verify(authExecutor).logout(idCaptor.capture(), anyBoolean());

        assertEquals(id, idCaptor.getValue());
    }

}
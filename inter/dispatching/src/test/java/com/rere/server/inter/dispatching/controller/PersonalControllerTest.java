package com.rere.server.inter.dispatching.controller;

import com.rere.server.inter.dto.response.AccountResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PersonalControllerTest extends AbstractControllerTest {

    @Test
    void getMeFailsForNoAuth() throws Exception {
        assertForbidden(get("/me/"));
    }

    @Test
    void getMeCallsExecutorAndReturns() throws Exception {
        UUID id = UUID.randomUUID();
        when(personalExecutor.getMe()).thenReturn(
                new AccountResponse(id.toString(), Instant.now().toString(), "email", "username", 55, "active")
        );

        setupAuth();
        client.perform(get("/me/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.profile_color").value(55));
    }

    @Test
    void updateMeFailsForNoAuth() throws Exception {
        assertForbidden(post("/me/"));
    }

    @Test
    void updateMeCallsExecutorAndReturns() throws Exception {
        UUID id = UUID.randomUUID();
        when(personalExecutor.updateMe(any())).thenReturn(
                new AccountResponse(id.toString(), Instant.now().toString(), "email", "username", 55, "active")
        );

        String content = """
                {
                "email": "new@email.com",
                "profile_color": 31,
                "username": "new123"
                }
                """;

        setupAuth();
        client.perform(post("/me/").content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.profile_color").value(55));
    }

}
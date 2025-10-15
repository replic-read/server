package com.rere.server.inter.dispatching.security;

import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.inter.dispatching.controller.AbstractMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("load-test")
class LoadTestSecurityConfigTest extends AbstractMvcTest {

    @Test
    void accountIsCreatedIfMissingAndWhitelist() throws Exception {
        String username = "load-test-user-" + UUID.randomUUID();
        String password = "password";
        String email = username + "@example.com";
        byte[] basicAuthBytes = Base64.getEncoder().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        String basicAuth = new String(basicAuthBytes, StandardCharsets.UTF_8);

        when(authService.authenticateWithCredentials(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(authService.createAccount(email, username, password, 0, true, true, false, true))
                .thenReturn(AccountImpl.builder()
                        .username(username)
                        .email(email)
                        .passwordHash(password)
                        .build());

        client.perform(get("/me/")
                        .header("Authorization", "Basic " + basicAuth))
                .andExpect(status().isOk());

        verify(authService).createAccount(email, username, password, 0, true, true, false, true);
    }

    @Test
    void accountNotCreatedIfFound() throws Exception {
        String username = "user123";
        String password = "password";
        byte[] basicAuthBytes = Base64.getEncoder().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        String basicAuth = new String(basicAuthBytes, StandardCharsets.UTF_8);

        when(authService.authenticateWithCredentials(null, username, password))
                .thenReturn(Optional.ofNullable(AccountImpl.builder().build()));

        client.perform(get("/me/")
                        .header("Authorization", "Basic " + basicAuth))
                .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedIfNoAuth() throws Exception {
        client.perform(get("/me/"))
                .andExpect(status().isForbidden());
    }

}

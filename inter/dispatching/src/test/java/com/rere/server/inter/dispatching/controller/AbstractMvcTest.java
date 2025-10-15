package com.rere.server.inter.dispatching.controller;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.domain.model.report.ReportState;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.AuthenticationService;
import com.rere.server.inter.dispatching.WebMvcConfig;
import com.rere.server.inter.dispatching.security.AccessTokenFilter;
import com.rere.server.inter.dispatching.security.SecurityConfig;
import com.rere.server.inter.dispatching.security.WhitelistBasicAuthFilter;
import com.rere.server.inter.dto.parameter.AccountSortParameter;
import com.rere.server.inter.dto.parameter.ReplicSortParameter;
import com.rere.server.inter.dto.parameter.ReportSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.execution.AccountExecutor;
import com.rere.server.inter.execution.AdminPanelExecutor;
import com.rere.server.inter.execution.AuthenticationExecutor;
import com.rere.server.inter.execution.PersonalExecutor;
import com.rere.server.inter.execution.ReplicExecutor;
import com.rere.server.inter.execution.ReportExecutor;
import com.rere.server.inter.execution.ServerConfigExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.rere.server.inter.dispatching.WebMvcConfig.BASE_PATH_PREFIX;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Abstract configuration for all mvc tests.
 */
@SpringBootTest(classes = AbstractMvcTest.Config.class)
public abstract class AbstractMvcTest {

    @MockitoBean
    protected AuthenticationExecutor authExecutor;

    @MockitoBean
    protected AccountExecutor<AccountSortParameter, AccountState, SortDirectionParameter, UUID> accountExecutor;

    @MockitoBean
    protected ReplicExecutor<ReplicState, ReplicSortParameter, SortDirectionParameter, UUID, InputStream> replicExecutor;

    @MockitoBean
    protected ReportExecutor<ReportState, ReportSortParameter, SortDirectionParameter, UUID> reportExecutor;

    @MockitoBean
    protected ServerConfigExecutor configExecutor;

    @MockitoBean
    protected AdminPanelExecutor adminExecutor;

    @MockitoBean
    protected PersonalExecutor personalExecutor;

    protected String jwt = null;
    @MockitoBean
    protected AuthenticationService authService;

    @Autowired
    protected WebApplicationContext context;

    /**
     * The mock http client.
     */
    protected MockMvc client;
    @MockitoBean
    private AccountService accountService;

    protected void setupAuth() {
        jwt = "<mock-jwt-token-" + Instant.now().getEpochSecond() + ">";
        Account acc = AccountImpl.builder()
                .email("user@gmail.com")
                .username("user123")
                .passwordHash("<hashed password>")
                .build();
        when(authService.authenticateWithJwt(jwt))
                .thenReturn(Optional.of(acc));
        when(accountService.getByEmail(acc.getEmail()))
                .thenReturn(Optional.of(acc));
    }

    private MockHttpServletRequestBuilder maybeAuthenticate(MockHttpServletRequestBuilder request) {
        if (jwt != null) {
            request.header("Authorization", "Bearer " + jwt);
        }

        return request;
    }

    /**
     * Performs a get request.
     */
    protected MockHttpServletRequestBuilder get(String uriTemplate) {
        return maybeAuthenticate(MockMvcRequestBuilders.get(BASE_PATH_PREFIX + uriTemplate).contentType("application/json"));
    }

    /**
     * Performs a post request.
     */
    protected MockHttpServletRequestBuilder post(String uriTemplate) {
        return maybeAuthenticate(MockMvcRequestBuilders.post(BASE_PATH_PREFIX + uriTemplate).contentType("application/json"));
    }

    /**
     * Performs a put request.
     */
    protected MockHttpServletRequestBuilder put(String uriTemplate) {
        return maybeAuthenticate(MockMvcRequestBuilders.put(BASE_PATH_PREFIX + uriTemplate).contentType("application/json"));
    }

    /**
     * Asserts that a provided requests causes a status 403.
     */
    protected void assertForbidden(MockHttpServletRequestBuilder request) throws Exception {
        client.perform(request)
                .andExpect(status().isForbidden());
    }

    @BeforeEach
    void setUp() {
        client = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        jwt = null;
    }

    /**
     * Custom test-config class.
     */
    @EnableWebMvc
    @Configuration
    @Import({WebMvcConfig.class, WhitelistBasicAuthFilter.class, AccessTokenFilter.class, SecurityConfig.class, AuthenticationController.class, AccountController.class, AdminPanelController.class, PersonalController.class, ReplicController.class, ReportController.class, ServerConfigController.class,})
    static class Config {

        @Bean
        protected PasswordEncoder noopEncoder() {
            return new PasswordEncoder() {
                @Override
                public String encode(CharSequence rawPassword) {
                    return rawPassword.toString();
                }

                @Override
                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                    return rawPassword.toString().equals(encodedPassword);
                }
            };
        }

    }

}

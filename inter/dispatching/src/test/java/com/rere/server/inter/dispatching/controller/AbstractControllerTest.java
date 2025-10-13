package com.rere.server.inter.dispatching.controller;

import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.domain.model.report.ReportState;
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
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.InputStream;
import java.util.UUID;

/**
 * Abstract configuration for all mvc tests.
 */
@SpringBootTest(classes = AbstractControllerTest.Config.class)
abstract class AbstractControllerTest {

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

    @Autowired
    protected WebApplicationContext context;

    /**
     * The mock http client.
     */
    protected MockMvc client;

    @BeforeEach
    void setUp() {
        client = MockMvcBuilders.webAppContextSetup(context).build();
    }

    protected MockHttpServletRequestBuilder get(String uriTemplate, Object... uriVariables) {
        return MockMvcRequestBuilders.get(uriTemplate, uriVariables).contentType("application/json");
    }

    protected MockHttpServletRequestBuilder post(String uriTemplate, Object... uriVariables) {
        return MockMvcRequestBuilders.post(uriTemplate, uriVariables).contentType("application/json");
    }

    protected MockHttpServletRequestBuilder put(String uriTemplate, Object... uriVariables) {
        return MockMvcRequestBuilders.put(uriTemplate, uriVariables).contentType("application/json");
    }

    /**
     * Custom test-config class.
     */
    @EnableWebMvc
    @Configuration
    @Import({AuthenticationController.class, AccountController.class, AdminPanelController.class, PersonalController.class, ReplicController.class, ReportController.class, ServerConfigController.class,})
    static class Config {
    }

}

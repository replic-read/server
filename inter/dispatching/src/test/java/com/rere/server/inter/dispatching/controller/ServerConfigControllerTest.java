package com.rere.server.inter.dispatching.controller;

import com.rere.server.inter.dto.response.ServerConfigResponse;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServerConfigControllerTest extends AbstractControllerTest {

    @Test
    void getServerConfigCallsExecutorAndReturns() throws Exception {
        when(configExecutor.getServerConfig())
                .thenReturn(
                        new ServerConfigResponse("all", "verified", "account",
                                "P4D", null, null, null,
                                false)
                );

        client.perform(get("/server-config/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.create_replic_group").value("all"))
                .andExpect(jsonPath("$.allow_signup").value("false"));
    }

    @Test
    void setServerConfigCallsExecutorAndReturns() throws Exception {
        when(configExecutor.setServerConfig(any()))
                .thenReturn(
                        new ServerConfigResponse("all", "verified", "account",
                                "P4D", null, null, null,
                                false)
                );

        String content = """
                {
                  "create_replic_group": "all",
                  "access_replic_group": "verified",
                  "create_report_group": "account",
                  "maximum_expiration_period": "P1Y",
                  "replic_limit_period": "P1Y",
                  "replic_limit_count": 500,
                  "allow_signup": true
                }
                """;

        client.perform(put("/server-config/").content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.create_replic_group").value("all"))
                .andExpect(jsonPath("$.allow_signup").value("false"));
    }


}
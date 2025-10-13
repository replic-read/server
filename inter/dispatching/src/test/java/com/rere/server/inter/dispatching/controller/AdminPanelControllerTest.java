package com.rere.server.inter.dispatching.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminPanelControllerTest extends AbstractControllerTest {

    @Test
    void shutdownCallsExecutorAndReturns() throws Exception {
        client.perform(post("/admin/shutdown/"))
                .andExpect(status().isOk());
    }

}
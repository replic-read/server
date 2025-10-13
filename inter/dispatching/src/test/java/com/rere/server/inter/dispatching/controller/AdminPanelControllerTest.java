package com.rere.server.inter.dispatching.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminPanelControllerTest extends AbstractControllerTest {

    @Test
    void shutdownFailsForNoAuth() throws Exception {
        assertForbidden(post("/admin/shutdown/"));
    }

    @Test
    void shutdownCallsExecutorAndReturns() throws Exception {
        setupAuth();
        client.perform(post("/admin/shutdown/"))
                .andExpect(status().isOk());
    }

}
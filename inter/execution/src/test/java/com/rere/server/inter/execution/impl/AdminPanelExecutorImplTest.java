package com.rere.server.inter.execution.impl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AdminPanelExecutorImplTest extends BaseExecutorTest {

    @InjectMocks
    private AdminPanelExecutorImpl subject;

    @Test
    void shutdownPropagatesAuthorization() {
        assertAuthorizationIsPropagated(() -> subject.shutdown());
    }

    @Test
    void shutdownDoesntThrow() {
        assertDoesNotThrow(() -> subject.shutdown());
    }

}
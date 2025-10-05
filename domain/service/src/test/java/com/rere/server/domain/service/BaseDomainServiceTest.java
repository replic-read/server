package com.rere.server.domain.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;

/**
 * Makes basic configuration to test classes.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseDomainServiceTest {

    private AutoCloseable closeable;

    @BeforeAll
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    void tearDown() throws Exception {
        closeable.close();
    }

}

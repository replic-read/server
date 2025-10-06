package com.rere.server.domain.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Makes basic configuration to test classes.
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseDomainServiceTest {

    /**
     * Creates a new URl from a given string and wraps the exception handling.
     * @param url The url string.
     * @return The created url.
     */
    protected static URL createUrl(String url) {
        try {
            return URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}

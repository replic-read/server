package com.rere.server.domain.service;

import com.rere.server.domain.model.exception.NotUniqueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Component that listens to context refreshes and ensures that only one admin account exists.
 */
@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(StartupListener.class);
    private final AuthenticationService authService;

    @Autowired
    public StartupListener(AuthenticationService authService) {
        this.authService = authService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            authService.ensureSingletonAdmin();
        } catch (NotUniqueException e) {
            log.error("Was not able to register a new admin account.", e);
        }
    }
}

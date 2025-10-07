package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.config.ReplicLimitConfig;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.repository.ServerConfigRepository;
import com.rere.server.domain.service.BaseDomainServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.time.Period;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * tests for the {@link ServerConfigServiceImpl} class.
 */
class ServerConfigServiceImplTest extends BaseDomainServiceTest {

    @Mock
    private ServerConfigRepository configRepo;

    @InjectMocks
    private ServerConfigServiceImpl subject;

    private static ServerConfig createConfig() {
        return new ServerConfig(
                AuthUserGroup.VERIFIED,
                AuthUserGroup.ACCOUNT,
                AuthUserGroup.ALL,
                false,
                new ReplicLimitConfig(
                        Period.of(1, 0, 0),
                        5,
                        Instant.now()
                ),
                Period.of(0, 1, 2)
        );
    }

    @Test
    void saveDelegatesToRepo() {
        ServerConfig config = createConfig();

        subject.save(config);

        ArgumentCaptor<ServerConfig> configCaptor = ArgumentCaptor.forClass(ServerConfig.class);
        verify(configRepo, times(1)).save(configCaptor.capture());

        Assertions.assertEquals(config, configCaptor.getValue());
    }

    @Test
    void getUsesDefault() {
        when(configRepo.get()).thenReturn(Optional.empty());

        ServerConfig config = subject.get();

        Assertions.assertNotNull(config);
    }

    @Test
    void getDelegatesToRepo() {
        ServerConfig config = createConfig();
        when(configRepo.get()).thenReturn(Optional.of(config));

        ServerConfig returned = subject.get();

        Assertions.assertEquals(config, returned);
    }

}

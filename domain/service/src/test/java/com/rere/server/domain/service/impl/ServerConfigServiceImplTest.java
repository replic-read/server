package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.impl.ServerConfigImpl;
import com.rere.server.domain.repository.ServerConfigRepository;
import com.rere.server.domain.service.BaseDomainServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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

    @Test
    void saveDelegatesToRepo() {
        ServerConfig config = ServerConfigImpl.builder().build();

        subject.save(config);

        ArgumentCaptor<ServerConfig> configCaptor = ArgumentCaptor.forClass(ServerConfig.class);
        verify(configRepo, times(1)).saveConfig(configCaptor.capture());

        Assertions.assertEquals(config, configCaptor.getValue());
    }

    @Test
    void getUsesDefault() {
        when(configRepo.getConfig()).thenReturn(Optional.empty());

        ServerConfig config = subject.get();

        Assertions.assertNotNull(config);
    }

    @Test
    void getDelegatesToRepo() {
        ServerConfig config = ServerConfigImpl.builder().build();
        when(configRepo.getConfig()).thenReturn(Optional.of(config));

        ServerConfig returned = subject.get();

        Assertions.assertEquals(config, returned);
    }

}

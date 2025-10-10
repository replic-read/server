package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.impl.ReplicLimitConfigImpl;
import com.rere.server.domain.model.impl.ServerConfigImpl;
import com.rere.server.domain.repository.ServerConfigRepository;
import com.rere.server.domain.service.BaseDomainServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Clock;
import java.time.Instant;
import java.time.Period;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * tests for the {@link ServerConfigServiceImpl} class.
 */
class ServerConfigServiceImplTest extends BaseDomainServiceTest {

    @Mock
    private ServerConfigRepository configRepo;
    @Mock
    private Clock clock;

    @InjectMocks
    private ServerConfigServiceImpl subject;

    @Test
    void saveDelegatesToRepo() {
        ServerConfig config = ServerConfigImpl.builder().build();

        subject.save(config);

        ArgumentCaptor<ServerConfig> configCaptor = ArgumentCaptor.forClass(ServerConfig.class);
        verify(configRepo, times(1)).saveConfig(configCaptor.capture());

        assertEquals(config, configCaptor.getValue());
    }

    @Test
    void saveSetsPeriodWhenLimitAdded() {
        Instant now = Instant.now();

        ServerConfig current = ServerConfigImpl.builder()
                .limit(null).build();

        when(clock.instant()).thenReturn(now);
        when(configRepo.getConfig()).thenReturn(Optional.ofNullable(current));

        ServerConfig newConfig = ServerConfigImpl.builder()
                .limit(ReplicLimitConfigImpl.builder()
                        .count(10)
                        .period(Period.of(1, 0, 0))
                        .build())
                .build();

        subject.save(newConfig);

        ArgumentCaptor<ServerConfig> configCaptor = ArgumentCaptor.captor();

        verify(configRepo, times(1)).saveConfig(configCaptor.capture());

        assertEquals(now, configCaptor.getValue().getLimit().getPeriodStart());
        assertEquals(10, configCaptor.getValue().getLimit().getCount());
        assertEquals(Period.of(1, 0, 0), configCaptor.getValue().getLimit().getPeriod());
    }

    @Test
    void saveSetsPeriodWhenLimitChanged() {
        Instant now = Instant.now();

        ServerConfig current = ServerConfigImpl.builder()
                .limit(ReplicLimitConfigImpl.builder()
                        .count(10)
                        .period(Period.of(1, 0, 0))
                        .build()).build();

        when(clock.instant()).thenReturn(now);
        when(configRepo.getConfig()).thenReturn(Optional.ofNullable(current));

        ServerConfig newConfig = ServerConfigImpl.builder()
                .limit(ReplicLimitConfigImpl.builder()
                        .count(10)
                        .period(Period.of(1, 0, 1))
                        .build())
                .build();

        subject.save(newConfig);

        ArgumentCaptor<ServerConfig> configCaptor = ArgumentCaptor.captor();

        verify(configRepo, times(1)).saveConfig(configCaptor.capture());

        assertEquals(now, configCaptor.getValue().getLimit().getPeriodStart());
        assertEquals(10, configCaptor.getValue().getLimit().getCount());
        assertEquals(Period.of(1, 0, 1), configCaptor.getValue().getLimit().getPeriod());
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

        assertEquals(config, returned);
    }

}

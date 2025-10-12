package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.impl.ReplicLimitConfigImpl;
import com.rere.server.domain.model.impl.ServerConfigImpl;
import com.rere.server.inter.dto.request.ServerConfigRequest;
import com.rere.server.inter.dto.response.ServerConfigResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.time.Instant;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServerConfigExecutorImplTest extends BaseExecutorTest {

    @InjectMocks
    private ServerConfigExecutorImpl subject;

    @Test
    void getServerConfigCorrectlyConvertsToResponse() {
        Instant now = Instant.now();
        ServerConfig config1 = ServerConfigImpl.builder()
                .accessReplicsGroup(AuthUserGroup.ALL)
                .createReportsGroup(AuthUserGroup.VERIFIED)
                .limit(ReplicLimitConfigImpl.builder()
                        .count(4)
                        .periodStart(now)
                        .period(Period.of(1, 0, 0))
                        .build())
                .allowAccountCreation(true)
                .build();
        ServerConfig config2 = ServerConfigImpl.builder()
                .accessReplicsGroup(AuthUserGroup.VERIFIED)
                .createReportsGroup(AuthUserGroup.ACCOUNT)
                .limit(null)
                .allowAccountCreation(false)
                .build();

        when(configService.get()).thenReturn(config1);

        ServerConfigResponse response1 = subject.getServerConfig();
        assertEquals("all", response1.accessReplicGroup());
        assertEquals("verified", response1.createReportGroup());
        assertEquals(4, response1.replicLimitCount());
        assertEquals(now.toString(), response1.replicLimitStart());
        assertEquals(Period.of(1, 0, 0).toString(), response1.replicLimitPeriod());
        assertTrue(response1.allowSignup());

        when(configService.get()).thenReturn(config2);

        ServerConfigResponse response2 = subject.getServerConfig();
        assertEquals("verified", response2.accessReplicGroup());
        assertEquals("account", response2.createReportGroup());
        assertNull(response2.replicLimitCount());
        assertNull(response2.replicLimitStart());
        assertNull(response2.replicLimitPeriod());
        assertFalse(response2.allowSignup());
    }

    @Test
    void setServerConfigPropagatesAuthorization() {
        assertAuthorizationIsPropagated(() -> subject.setServerConfig(null));
    }

    @Test
    void setServerConfigCreateConfigWithoutLimit() {
        when(configService.get()).thenReturn(ServerConfigImpl.builder().build());

        ServerConfigRequest request = new ServerConfigRequest(
                "all",
                "verified",
                "account",
                "P3Y1M",
                "blahh",
                null,
                true
        );
        subject.setServerConfig(request);

        ArgumentCaptor<ServerConfig> configCaptor = ArgumentCaptor.captor();
        verify(configService).save(configCaptor.capture());

        assertEquals(Period.of(3, 1, 0), configCaptor.getValue().getMaximumActivePeriod());
        assertNull(configCaptor.getValue().getLimit());
        assertEquals(AuthUserGroup.ALL, configCaptor.getValue().getCreateReplicsGroup());
        assertEquals(AuthUserGroup.VERIFIED, configCaptor.getValue().getAccessReplicsGroup());
        assertEquals(AuthUserGroup.ACCOUNT, configCaptor.getValue().getCreateReportsGroup());
        assertTrue(configCaptor.getValue().isAllowAccountCreation());
    }

    @Test
    void setServerConfigCreateConfigWithLimit() {
        when(configService.get()).thenReturn(ServerConfigImpl.builder().build());

        ServerConfigRequest request = new ServerConfigRequest(
                "all",
                "verified",
                "account",
                "P3Y1M",
                "P5Y1D",
                2,
                true
        );
        subject.setServerConfig(request);

        ArgumentCaptor<ServerConfig> configCaptor = ArgumentCaptor.captor();
        verify(configService).save(configCaptor.capture());

        assertEquals(Period.of(3, 1, 0), configCaptor.getValue().getMaximumActivePeriod());
        assertEquals(Period.of(5, 0, 1), configCaptor.getValue().getLimit().getPeriod());
        assertEquals(2, configCaptor.getValue().getLimit().getCount());
        assertEquals(AuthUserGroup.ALL, configCaptor.getValue().getCreateReplicsGroup());
        assertEquals(AuthUserGroup.VERIFIED, configCaptor.getValue().getAccessReplicsGroup());
        assertEquals(AuthUserGroup.ACCOUNT, configCaptor.getValue().getCreateReportsGroup());
        assertTrue(configCaptor.getValue().isAllowAccountCreation());
    }

}
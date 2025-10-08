package com.rere.server.domain.model.impl;

import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.config.ReplicLimitConfig;
import com.rere.server.domain.model.config.ServerConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Period;

@Data
@AllArgsConstructor
@Builder
public class ServerConfigImpl implements ServerConfig {

    @Builder.Default
    private AuthUserGroup createReplicsGroup = AuthUserGroup.ALL;

    @Builder.Default
    private AuthUserGroup accessReplicsGroup = AuthUserGroup.ALL;

    @Builder.Default
    private AuthUserGroup createReportsGroup = AuthUserGroup.ALL;

    @Builder.Default
    private boolean allowAccountCreation = true;

    @Builder.Default
    private ReplicLimitConfig limit = null;

    @Builder.Default
    private Period maximumActivePeriod = null;

}

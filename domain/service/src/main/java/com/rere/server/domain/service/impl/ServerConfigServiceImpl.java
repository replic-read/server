package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.config.ReplicLimitConfig;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.impl.ServerConfigImpl;
import com.rere.server.domain.repository.ServerConfigRepository;
import com.rere.server.domain.service.ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Implementation of the server config service.
 */
@Component
public class ServerConfigServiceImpl implements ServerConfigService {

    private final ServerConfigRepository configRepo;

    private final Clock clock;

    @Autowired
    public ServerConfigServiceImpl(ServerConfigRepository configRepo, Clock clock) {
        this.configRepo = configRepo;
        this.clock = clock;
    }

    @Override
    public void save(ServerConfig config) {
        ServerConfig current = get();
        boolean limitWasAdded = current.getLimit() == null && config.getLimit() != null;
        boolean limitPeriodWasChanged = current.getLimit() != null && config.getLimit() != null && !current.getLimit().equals(config.getLimit());

        if (limitWasAdded || limitPeriodWasChanged) {
            ReplicLimitConfig limit = config.getLimit();
            limit.setPeriodStart(clock.instant());
        }

        configRepo.saveConfig(config);
    }

    /**
     * Server config that is the least restrictive. Used as a initial value.
     */
    private static final ServerConfig UNRESTRICTED_CONFIG = new ServerConfigImpl(
            AuthUserGroup.ALL,
            AuthUserGroup.ALL,
            AuthUserGroup.ALL,
            true,
            null,
            null
    );

    @Override
    public ServerConfig get() {
        ServerConfig config =  configRepo.getConfig().orElse(UNRESTRICTED_CONFIG);
        configRepo.saveConfig(config);
        return config;
    }
}

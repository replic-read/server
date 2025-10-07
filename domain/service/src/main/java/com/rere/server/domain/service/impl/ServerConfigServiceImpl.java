package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.repository.ServerConfigRepository;
import com.rere.server.domain.service.ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of the server config service.
 */
public class ServerConfigServiceImpl implements ServerConfigService {

    private final ServerConfigRepository configRepo;

    @Autowired
    public ServerConfigServiceImpl(ServerConfigRepository configRepo) {
        this.configRepo = configRepo;
    }

    @Override
    public void save(ServerConfig config) {
        configRepo.saveConfig(config);
    }

    /**
     * Server config that is the least restrictive. Used as a initial value.
     */
    private static final ServerConfig UNRESTRICTED_CONFIG = new ServerConfig(
            AuthUserGroup.ALL,
            AuthUserGroup.ALL,
            AuthUserGroup.ALL,
            true,
            null,
            null
    );

    @Override
    public ServerConfig get() {
        return configRepo.getConfig().orElse(UNRESTRICTED_CONFIG);
    }
}

package com.rere.server.domain.service;

import com.rere.server.domain.model.config.ServerConfig;

/**
 * Service that provides access to the server config.
 */
public interface ServerConfigService {

    /**
     * Sets the config.
     *
     * @param config The config.
     */
    void save(ServerConfig config);

    /**
     * Gets the server config.
     *
     * @return The config.
     */
    ServerConfig get();

}

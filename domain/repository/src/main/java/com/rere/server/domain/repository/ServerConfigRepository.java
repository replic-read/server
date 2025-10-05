package com.rere.server.domain.repository;

import com.rere.server.domain.model.config.ServerConfig;

import java.util.Optional;

/**
 * Repository for {@link ServerConfig}
 */
public interface ServerConfigRepository {

    /**
     * Gets the saved {@link ServerConfig} or an empty optional if it doesnt exist yet.
     * @return The config, or empty if it doesn#t exist yet.
     */
    Optional<ServerConfig> get();

    /**
     * Saves a {@link ServerConfig}.
     * @param serverConfig The config to save.
     */
    void save(ServerConfig serverConfig);

}

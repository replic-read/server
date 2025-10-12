package com.rere.server.inter.execution;

import com.rere.server.inter.dto.request.ServerConfigRequest;
import com.rere.server.inter.dto.response.ServerConfigResponse;

/**
 * Executor for requests with the server config.
 */
public interface ServerConfigExecutor {

    /**
     * Executor for GET /server-config/.
     * @return The response body.
     */
    ServerConfigResponse getServerConfig();

    /**
     * Endpoint for POST /server-config/.
     * @param request The request body.
     * @return The response body.
     */
    ServerConfigResponse setServerConfig(ServerConfigRequest request);

}

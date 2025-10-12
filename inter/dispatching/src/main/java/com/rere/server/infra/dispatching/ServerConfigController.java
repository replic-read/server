package com.rere.server.infra.dispatching;

import com.rere.server.infra.dispatching.documentation.endpoint.EndpointMetadata;
import com.rere.server.inter.dto.request.ServerConfigRequest;
import com.rere.server.inter.dto.response.ServerConfigResponse;
import com.rere.server.inter.execution.ServerConfigExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rere.server.infra.dispatching.documentation.endpoint.ApiResponseType.NO_PERMISSION_NO_EXIST;
import static com.rere.server.infra.dispatching.documentation.endpoint.ApiResponseType.SUCCESS;
import static com.rere.server.infra.dispatching.documentation.endpoint.AuthorizationType.ADMIN;

/**
 * The web-controller for server-config matters.
 * <br>
 * Implements ServerConfigExecutor as semantic detail.
 * We don't need the polymorphism, but as this class acts as a proxy, it makes sense to implement the interface.
 */
@Tag(
        name = "Server configuration",
        description = "Handles querying and setting of server config."
)
@RestController
@RequestMapping("/server-config")
public class ServerConfigController implements ServerConfigExecutor {

    private final ServerConfigExecutor executor;

    @Autowired
    public ServerConfigController(ServerConfigExecutor executor) {
        this.executor = executor;
    }

    @Operation(summary = "Get the server config", description = "Gets the server config.")
    @EndpointMetadata(responseTypes = SUCCESS)
    @GetMapping("/")
    @Override
    public ServerConfigResponse getServerConfig() {
        return executor.getServerConfig();
    }

    @Operation(summary = "Set the server config", description = "Sets the server config. Accessible for admin accounts.")
    @EndpointMetadata(
            authorizationType = ADMIN,
            responseTypes = {SUCCESS, NO_PERMISSION_NO_EXIST}
    )
    @PutMapping("/")
    @Override
    public ServerConfigResponse setServerConfig(@Valid @RequestBody ServerConfigRequest request) {
        return executor.setServerConfig(request);
    }
}

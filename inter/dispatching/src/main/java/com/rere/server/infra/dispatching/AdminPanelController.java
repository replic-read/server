package com.rere.server.infra.dispatching;

import com.rere.server.infra.dispatching.documentation.endpoint.EndpointMetadata;
import com.rere.server.inter.execution.AdminPanelExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rere.server.infra.dispatching.documentation.endpoint.ApiResponseType.BAD_AUTHENTICATION;
import static com.rere.server.infra.dispatching.documentation.endpoint.ApiResponseType.NO_PERMISSION_NO_EXIST;
import static com.rere.server.infra.dispatching.documentation.endpoint.ApiResponseType.SUCCESS;
import static com.rere.server.infra.dispatching.documentation.endpoint.AuthorizationType.ADMIN;

/**
 * The web-controller for admin matters.
 * <br>
 * Implements AdminPanelExecutor as semantic detail.
 * We don't need the polymorphism, but as this class acts as a proxy, it makes sense to implement the interface.
 */
@Tag(
        name = "Admin panel",
        description = "Handles miscellaneous actions available for the admins."
)
@RestController
@RequestMapping("/admin")
public class AdminPanelController implements AdminPanelExecutor {

    private final AdminPanelExecutor executor;

    @Autowired
    public AdminPanelController(AdminPanelExecutor executor) {
        this.executor = executor;
    }

    @Operation(
            summary = "Shutdown server",
            description = "Shuts the server down."
    )
    @EndpointMetadata(
            authorizationType = ADMIN,
            responseTypes = {SUCCESS,
                    NO_PERMISSION_NO_EXIST,
                    BAD_AUTHENTICATION}
    )
    @PostMapping("/shutdown/")
    @Override
    public void shutdown() {
        executor.shutdown();
    }
}

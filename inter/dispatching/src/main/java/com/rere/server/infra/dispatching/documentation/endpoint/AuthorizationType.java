package com.rere.server.infra.dispatching.documentation.endpoint;

import lombok.Getter;

/**
 * Defines the different kinds of authorizations expected by an endpoint.
 */
@Getter
public enum AuthorizationType {

    NONE("No specific authorization is required."),

    LOGGED_IN("The authorization needs to point to an account."),

    ADMIN("Only admins are authorized to access this endpoint."),

    CONFIG_SPECIFIC("The specific authorization requirement depends on the server's config."),

    REPLIC_STATUS_UPDATE("Changing the replica's state from/to 'removed' requires admin authorization, changing the state from/to 'inactive' requires owner authorization."),
    ;

    /**
     * Description of the authorization.o
     */
    private final String description;

    AuthorizationType(String description) {
        this.description = description;
    }
}
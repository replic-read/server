package com.rere.server.inter.authorization;

import com.rere.server.domain.model.config.AuthUserGroup;
import lombok.Getter;

/**
 * Exception thrown by the {@link Authorizer} to signify that a client was not authorized to perform a specific operation.
 */
@Getter
public class AuthorizationException extends RuntimeException {

    /**
     * The http status code to return.
     */
    private final int status;

    private AuthorizationException(String message, int status) {
        super(message);
        this.status = status;
    }

    /**
     * Creates a new AuthorizationException that denotes that an operation is only accessible to admins.
     * @return The new exception.
     */
    public static AuthorizationException onlyAdmins() {
        return new AuthorizationException("This operation is only accessible to admins.", 401);
    }

    /**
     * Creates a new AuthorizationException that denotes that an operation is only accessible to a specific {@link AuthUserGroup}.
     * @param group The group.
     * @return The new exception.
     */
    public static AuthorizationException onlyGroup(AuthUserGroup group) {
        String message = switch (group) {
            case ACCOUNT -> "This operation is only accessible to authenticated clients.";
            case VERIFIED -> "This operation is only accessible to verified clients.";
            case ALL ->
                    throw new IllegalArgumentException("The case of AuthUserGroup.ALL should never trigger an AuthorizationException.");
        };

        return new AuthorizationException(message, 401);
    }

    /**
     * Creates a new AuthorizationException that denotes that an operation was disabled by the server admins.
     * @return The new exception.
     */
    public static AuthorizationException disabledByConfig() {
        return new AuthorizationException("This operation was disabled by server admins.", 403);
    }

    /**
     * Creates a generic AuthorizationException with status 403.
     * @return The new exception.
     */
    public static AuthorizationException genericForbidden() {
        return new AuthorizationException(null, 403);
    }

    /**
     * Created a generic AuthorizationException with status 401.
     * @return The new exception.
     */
    public static AuthorizationException genericUnauthorized() {
        return new AuthorizationException(null, 401);
    }

}

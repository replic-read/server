package com.rere.server.infra.dispatching.documentation.endpoint;

import com.rere.server.inter.dto.response.AccountWithTokensResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Defines different semantic types a response body of an endpoint can have. <br>
 * Used for documentation-generation.
 */
@Getter
public enum ApiResponseType {

    SIGNUP_ACCOUNT_CREATED(HttpStatus.CREATED, "Account was created", AccountWithTokensResponse.class),
    SIGNUP_ACCOUNT_CREATED_EMAIL_SENT(HttpStatus.ACCEPTED, "Account was created and verification email was sent.", AccountWithTokensResponse.class),
    SIGNUP_DISABLED(HttpStatus.FORBIDDEN, "Creating account is disabled."),

    LOGIN_MISSING_IDENTIFICATION(HttpStatus.BAD_REQUEST, "Either username and email, or neither username nor email were provided."),

    REQUEST_EMAIL_TOKEN_SUCCESS(HttpStatus.ACCEPTED, "An email containing a link was sent."),
    REQUEST_EMAIL_TOKEN_ALREADY_VERIFIED(HttpStatus.CONFLICT, "The email of the account is already verified."),

    SUBMIT_EMAIL_TOKEN_BAD_TOKEN(HttpStatus.BAD_REQUEST, "The provided token was invalid."),

    CREATE_REPLIC_QUOTA_REACHED(HttpStatus.TOO_MANY_REQUESTS, "The account has reached the maximum created replics for the current period."),
    CREATE_REPLIC_BAD_EXPIRATION(HttpStatus.BAD_REQUEST, "If an expiration is required and no expiration was provided/an expiration that is too late was provided."),

    SUCCESS(HttpStatus.OK, "Operation was successful."),
    BAD_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "Either no authentication was presented, or the presented authentication was invalid."),
    NO_PERMISSION_NO_EXIST(HttpStatus.FORBIDDEN, "The resource does not exists, or the client does not have acces to it."),

    ACCOUNT_UNIQUE(HttpStatus.CONFLICT, "Username or email is already used."),

    ;

    /**
     * The response code for the response.
     */
    private final HttpStatus responseCode;

    /**
     * A description of when the response occurs.
     */
    private final String description;

    /**
     * An optional class that describes the response body. <br>
     * If none is present and the responseCode is 200, the return type of the method is used.
     */
    private final Class<?> contentClass;

    /**
     * Constructor for response types that want to set all parameters, mostly successful responses with custom text.
     *
     * @param responseCode The response code.
     * @param description The description of the response.
     * @param contentClass The return type.
     */
    ApiResponseType(HttpStatus responseCode, String description, Class<?> contentClass) {
        this.responseCode = responseCode;
        this.description = description;
        this.contentClass = contentClass;
    }

    /**
     * Constructor for response types that don't want to declare a response type.
     *
     * @param responseCode The status code.
     * @param description The description.
     */
    ApiResponseType(HttpStatus responseCode, String description) {
        this(responseCode, description, null);
    }

}

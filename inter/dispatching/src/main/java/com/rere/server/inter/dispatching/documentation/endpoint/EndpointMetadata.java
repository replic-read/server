package com.rere.server.inter.dispatching.documentation.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds metadata to a spring-mvc endpoint method.
 * Used for automatic documentation generation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EndpointMetadata {

    /**
     * The {@link ApiResponseType}s that the endpoint can return.
     *
     * @return The possible response types.
     */
    ApiResponseType[] responseTypes() default {};

    /**
     * The {@link AuthorizationType} that applies to the endpoint.
     *
     * @return The authorization type.
     */
    AuthorizationType authorizationType() default AuthorizationType.NONE;

}

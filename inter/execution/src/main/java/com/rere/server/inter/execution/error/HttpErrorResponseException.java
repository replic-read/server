package com.rere.server.inter.execution.error;

import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.InvalidExpirationException;
import com.rere.server.domain.model.exception.InvalidPasswordException;
import com.rere.server.domain.model.exception.InvalidTokenException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.exception.ReplicContentWriteException;
import com.rere.server.domain.model.exception.ReplicQuotaMetException;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * An exception that is thrown with the sole purpose of being caught.
 * This exception will be caught by a spring error handler.
 * It contains an http error status and possibly an error body.
 */
@Getter
public class HttpErrorResponseException extends RuntimeException {

    private static final Map<Class<? extends DomainException>, Integer> DOMAIN_EXCEPTION_CODES;

    static {
        DOMAIN_EXCEPTION_CODES = new HashMap<>();
        DOMAIN_EXCEPTION_CODES.put(InvalidExpirationException.class, 404);
        DOMAIN_EXCEPTION_CODES.put(InvalidPasswordException.class, 401);
        DOMAIN_EXCEPTION_CODES.put(InvalidTokenException.class, 404);
        DOMAIN_EXCEPTION_CODES.put(NotFoundException.class, 404);
        DOMAIN_EXCEPTION_CODES.put(NotUniqueException.class, 409);
        DOMAIN_EXCEPTION_CODES.put(OperationDisabledException.class, 403);
        DOMAIN_EXCEPTION_CODES.put(ReplicContentWriteException.class, 500);
        DOMAIN_EXCEPTION_CODES.put(ReplicQuotaMetException.class, 429);
    }

    /**
     * The error body.
     */
    @Nullable
    private final ErrorResponseInfo error;

    /**
     * The http error status.
     */
    private final int status;

    /**
     * Creates a new HttpErrorResponseException.
     * @param error The error body, or null.
     * @param status The http error status.
     */
    public HttpErrorResponseException(@Nullable ErrorResponseInfo error, int status) {
        this.error = error;
        this.status = status;
    }

    /**
     * Creates a new HttpErrorResponseException from a domain exception.
     * @param e The domain exception.
     * @return The error response exception.
     */
    public static HttpErrorResponseException fromDomainException(DomainException e) {
        return new HttpErrorResponseException(ErrorResponseInfo.fromDomainException(e),
                DOMAIN_EXCEPTION_CODES.get(e.getClass()));
    }
}

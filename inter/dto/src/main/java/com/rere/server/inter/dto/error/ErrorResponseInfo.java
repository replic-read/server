package com.rere.server.inter.dto.error;

import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.InvalidExpirationException;
import com.rere.server.domain.model.exception.InvalidTokenException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.exception.ReplicQuotaMetException;
import com.rere.server.inter.dto.error.domain.MessageBasedInfo;
import com.rere.server.inter.dto.error.domain.NotUniqueInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * A super class for every object that will later be serialized into an http-error body.
 */
@Getter
@EqualsAndHashCode
public abstract class ErrorResponseInfo implements Serializable {

    private final ErrorType errorType;

    /**
     * Creates a new ErrorResponseInfo.
     * @param errorType The type of error.
     */
    protected ErrorResponseInfo(ErrorType errorType) {
        this.errorType = errorType;
    }

    /**
     * Creates a new ErrorResponseInfo from a DomainException.
     * @param ex The domain exception.
     * @return The error response, or null if the domain exception doesn't require a specialized error body.
     */
    public static ErrorResponseInfo fromDomainException(DomainException ex) {
        return switch (ex) {
            case NotUniqueException e -> new NotUniqueInfo(e.getSubject());
            case InvalidExpirationException ignored -> new MessageBasedInfo("The provided expiration was invalid.");
            case InvalidTokenException ignored -> new MessageBasedInfo("The provided token was invalid.");
            case ReplicQuotaMetException ignored -> new MessageBasedInfo("You reached the limit of your replic quota.");
            case OperationDisabledException e -> new MessageBasedInfo(switch (e.getOperation()) {
                case REPORT -> "";
                case SIGNUP -> "Signing up is disabled.";
                case QUOTA_INFO -> "No quota is set up.";
            });
            default -> null;
        };
    }


}

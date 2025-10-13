package com.rere.server.inter.dto.error.domain;

import com.rere.server.domain.model.exception.NotFoundSubject;
import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.ErrorType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Error info about an error that was caused by a value not being found.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class NotFoundInfo extends ErrorResponseInfo {

    /**
     * The kind of value that was not found.
     */
    private final NotFoundSubject subject;

    /**
     * The identifier by which was searched.
     */
    private final Serializable identifier;

    /**
     * Creates a new NotFoundInfo.
     *
     * @param subject The subject that was not found.
     */
    public NotFoundInfo(NotFoundSubject subject, Serializable identifier) {
        super(ErrorType.NOT_FOUND);
        this.subject = subject;
        this.identifier = identifier;
    }
}

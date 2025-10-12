package com.rere.server.inter.dto.error.domain;

import com.rere.server.domain.model.exception.NotUniqueSubject;
import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.ErrorType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Error info about an error that was caused by a value not being unique.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class NotUniqueInfo extends ErrorResponseInfo {

    /**
     * The kind of value that was not unique.
     */
    private final NotUniqueSubject subject;

    /**
     * Creates a new NotUniqueInfo.
     * @param subject The subject that was not unique.
     */
    public NotUniqueInfo(NotUniqueSubject subject) {
        super(ErrorType.NON_UNIQUE);
        this.subject = subject;
    }
}

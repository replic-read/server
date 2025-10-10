package com.rere.server.inter.execution.error.domain;

import com.rere.server.domain.model.exception.NotUniqueSubject;
import com.rere.server.inter.execution.error.ErrorResponseInfo;
import com.rere.server.inter.execution.error.ErrorType;

public class NotUniqueInfo extends ErrorResponseInfo {

    private final NotUniqueSubject subject;

    public NotUniqueInfo(NotUniqueSubject subject) {
        super(ErrorType.NON_UNIQUE);
        this.subject = subject;
    }
}

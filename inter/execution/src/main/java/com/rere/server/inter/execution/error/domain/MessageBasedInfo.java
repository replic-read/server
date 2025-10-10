package com.rere.server.inter.execution.error.domain;

import com.rere.server.inter.execution.error.ErrorResponseInfo;
import com.rere.server.inter.execution.error.ErrorType;

public class MessageBasedInfo extends ErrorResponseInfo {

    private final String message;

    public MessageBasedInfo(String message) {
        super(ErrorType.MESSAGE);
        this.message = message;
    }
}

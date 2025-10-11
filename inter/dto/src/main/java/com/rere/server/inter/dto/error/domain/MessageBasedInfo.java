package com.rere.server.inter.dto.error.domain;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.ErrorType;
import lombok.Getter;

/**
 * Error info about an error that is only further describes by a message.
 */
@Getter
public class MessageBasedInfo extends ErrorResponseInfo {

    /**
     * The message describing the error.
     */
    private final String message;

    /**
     * Creates a new MessageBasedInfo.
     * @param message The message.o
     */
    public MessageBasedInfo(String message) {
        super(ErrorType.MESSAGE);
        this.message = message;
    }
}

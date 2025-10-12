package com.rere.server.inter.dto;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.validation.PatternErrorResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializationUtilsTest {

    @Test
    void serializationWorks() {
        ErrorResponseInfo info = new PatternErrorResponse("example-pattern", "value");
        assertEquals(info, SerializationUtils.fromBase64(SerializationUtils.toBase64(info)));
    }

}
package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.validation.AbstractValidationTest;

import java.util.UUID;

class RefreshRequestTest extends AbstractValidationTest<RefreshRequest> {

    @Override
    protected RefreshRequest[] getValidDtos() {
        return new RefreshRequest[]{
                new RefreshRequest(null),
                new RefreshRequest(UUID.randomUUID().toString()),
        };
    }

    @Override
    protected Pair<RefreshRequest, ? extends ErrorResponseInfo>[] getInvalidDtos() {
        return new Pair[0];
    }
}
package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.validation.AbstractValidationTest;

import java.util.UUID;

class SubmitEmailVerificationRequestTest extends AbstractValidationTest<SubmitEmailVerificationRequest> {

    @Override
    protected SubmitEmailVerificationRequest[] getValidDtos() {
        return new SubmitEmailVerificationRequest[]{
                new SubmitEmailVerificationRequest(null),
                new SubmitEmailVerificationRequest(UUID.randomUUID().toString()),
        };
    }

    @Override
    protected Pair<SubmitEmailVerificationRequest, ? extends ErrorResponseInfo>[] getInvalidDtos() {
        return new Pair[0];
    }
}
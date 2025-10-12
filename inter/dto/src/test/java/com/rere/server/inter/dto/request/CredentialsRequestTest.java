package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.validation.AbstractValidationTest;

class CredentialsRequestTest extends AbstractValidationTest<CredentialsRequest> {

    @Override
    protected CredentialsRequest[] getValidDtos() {
        return new CredentialsRequest[]{
                new CredentialsRequest(null, null, null),
                new CredentialsRequest("email", null, null),
                new CredentialsRequest("email", null, "password"),
                new CredentialsRequest("email", "username", "password")
        };
    }

    @Override
    protected Pair<CredentialsRequest, ? extends ErrorResponseInfo>[] getInvalidDtos() {
        return new Pair[0];
    }
}
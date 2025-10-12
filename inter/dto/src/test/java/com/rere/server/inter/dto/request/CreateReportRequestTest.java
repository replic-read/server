package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.validation.AbstractValidationTest;
import com.rere.server.inter.dto.validation.Pair;

class CreateReportRequestTest extends AbstractValidationTest<CreateReportRequest> {

    @Override
    protected CreateReportRequest[] getValidDtos() {
        return new CreateReportRequest[]{
                new CreateReportRequest("desc"),
                new CreateReportRequest(""),
                new CreateReportRequest(null),
        };
    }

    @Override
    protected Pair<CreateReportRequest, ? extends ErrorResponseInfo>[] getInvalidDtos() {
        return new Pair[0];
    }
}
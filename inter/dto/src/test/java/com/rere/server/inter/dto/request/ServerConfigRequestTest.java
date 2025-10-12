package com.rere.server.inter.dto.request;

import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.validation.EnumErrorResponse;
import com.rere.server.inter.dto.error.validation.SpecificFormatErrorResponse;
import com.rere.server.inter.dto.mapper.EnumMapper;
import com.rere.server.inter.dto.validation.AbstractValidationTest;
import com.rere.server.inter.dto.validation.Pair;
import com.rere.server.inter.dto.validation.SpecificFormat;

class ServerConfigRequestTest extends AbstractValidationTest<ServerConfigRequest> {

    @Override
    protected ServerConfigRequest[] getValidDtos() {
        return new ServerConfigRequest[]{
                new ServerConfigRequest("all", "verified", "account", "P7Y3M", null, null, false),
                new ServerConfigRequest("verified", "all", "account", null, "P3D", null, false),
                new ServerConfigRequest("verified", "all", "account", null, "P3D", 7, false),
                new ServerConfigRequest("verified", "all", "account", null, null, 7, false),
        };
    }

    @Override
    protected Pair<ServerConfigRequest, ? extends ErrorResponseInfo>[] getInvalidDtos() {
        return new Pair[]{
                new Pair(
                        new ServerConfigRequest("none", "verified", "account", "P7Y3M", null, null, false),
                        new EnumErrorResponse(EnumMapper.getAll(AuthUserGroup.class), "none")
                ),
                new Pair(
                        new ServerConfigRequest("all", "verified", "account", "three days", null, null, false),
                        new SpecificFormatErrorResponse("three days", SpecificFormat.JAVA_PERIOD)
                ),
                new Pair(
                        new ServerConfigRequest("all", "verified", "account", "P7Y", "five days", null, false),
                        new SpecificFormatErrorResponse("five days", SpecificFormat.JAVA_PERIOD)
                ),
                new Pair(
                        new ServerConfigRequest("all", "verified", "account", "P7Y", "P5D", -6, false),
                        new SpecificFormatErrorResponse(-6, SpecificFormat.POSITIVE_INTEGER)
                ),
        };
    }
}
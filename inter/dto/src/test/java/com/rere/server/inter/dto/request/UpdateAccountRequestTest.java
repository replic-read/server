package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.validation.PatternErrorResponse;
import com.rere.server.inter.dto.error.validation.SpecificFormatErrorResponse;
import com.rere.server.inter.dto.validation.AbstractValidationTest;
import com.rere.server.inter.dto.validation.SpecificFormat;
import com.rere.server.inter.dto.validation.ValidationPatterns;

class UpdateAccountRequestTest extends AbstractValidationTest<UpdateAccountRequest> {

    @Override
    protected UpdateAccountRequest[] getValidDtos() {
        return new UpdateAccountRequest[]{
                new UpdateAccountRequest("user@gmail.com", "username", 7272)
        };
    }

    @Override
    protected Pair<UpdateAccountRequest, ? extends ErrorResponseInfo>[] getInvalidDtos() {
        return new Pair[]{
                new Pair(
                        new UpdateAccountRequest("emailgmail.com", "username", 55),
                        new PatternErrorResponse(ValidationPatterns.EMAIL, "emailgmail.com")
                ),
                new Pair(
                        new UpdateAccountRequest("email@gmail.com", "usernameđđđ", 55),
                        new PatternErrorResponse(ValidationPatterns.USERNAME, "usernameđđđ")
                ),
                new Pair(
                        new UpdateAccountRequest("email@gmail.com", "username", -55),
                        new SpecificFormatErrorResponse(-55, SpecificFormat.POSITIVE_INTEGER)
                )
        };
    }
}
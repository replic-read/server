package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.validation.PatternErrorResponse;
import com.rere.server.inter.dto.error.validation.SpecificFormatErrorResponse;
import com.rere.server.inter.dto.validation.AbstractValidationTest;
import com.rere.server.inter.dto.validation.SpecificFormat;
import com.rere.server.inter.dto.validation.ValidationPatterns;

class CreateAccountRequestTest extends AbstractValidationTest<CreateAccountRequest> {

    @Override
    protected CreateAccountRequest[] getValidDtos() {
        return new CreateAccountRequest[]{
                new CreateAccountRequest("user@gmail.com", "password", 2, "username"),
                new CreateAccountRequest("2c92d039-0be2-4d56-89be-aafffa1e34dc@shop.io", "Em:e6h1z9QTj6V2qwVGV}Ln#YreXxUUQMN_kVo", 34579348, "user_656_"),
        };
    }

    @Override
    protected Pair<CreateAccountRequest, ? extends ErrorResponseInfo>[] getInvalidDtos() {
        return new Pair[]{
                new Pair<CreateAccountRequest, ErrorResponseInfo>(
                        new CreateAccountRequest("usergmail.com", "password", 0, "blahh"),
                        new PatternErrorResponse(ValidationPatterns.EMAIL, "usergmail.com")
                ),
                new Pair<CreateAccountRequest, ErrorResponseInfo>(
                        new CreateAccountRequest("user@gmail.com", "few", 0, "blahh"),
                        new PatternErrorResponse(ValidationPatterns.PASSWORD, "few")
                ),
                new Pair<CreateAccountRequest, ErrorResponseInfo>(
                        new CreateAccountRequest("user@gmail.com", "passw", -57839, "blahh"),
                        new SpecificFormatErrorResponse(-57839, SpecificFormat.POSITIVE_INTEGER)
                ),
                new Pair<CreateAccountRequest, ErrorResponseInfo>(
                        new CreateAccountRequest("user@gmail.com", "passw", 57839, "username_illegal_chars_@@←¶ſðſſ"),
                        new PatternErrorResponse(ValidationPatterns.USERNAME, "username_illegal_chars_@@←¶ſðſſ")
                )
        };
    }
}
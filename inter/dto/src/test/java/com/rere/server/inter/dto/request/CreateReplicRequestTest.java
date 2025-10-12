package com.rere.server.inter.dto.request;

import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.validation.EnumErrorResponse;
import com.rere.server.inter.dto.error.validation.PatternErrorResponse;
import com.rere.server.inter.dto.error.validation.SpecificFormatErrorResponse;
import com.rere.server.inter.dto.mapper.EnumMapper;
import com.rere.server.inter.dto.validation.AbstractValidationTest;
import com.rere.server.inter.dto.validation.SpecificFormat;
import com.rere.server.inter.dto.validation.ValidationPatterns;

import java.time.Instant;

class CreateReplicRequestTest extends AbstractValidationTest<CreateReplicRequest> {

    @Override
    protected CreateReplicRequest[] getValidDtos() {
        return new CreateReplicRequest[]{
                new CreateReplicRequest("https://google.com/", "all", Instant.now().toString(), "blah", "dsauh/§R§QO@ſð@ŧſŋſ¢„")
        };
    }

    @Override
    protected Pair<CreateReplicRequest, ? extends ErrorResponseInfo>[] getInvalidDtos() {
        return new Pair[]{
                new Pair(
                        new CreateReplicRequest("google.com/", "all", Instant.now().toString(), "blah", "dsauh/§R§QO@ſð@ŧſŋſ¢„"),
                        new SpecificFormatErrorResponse("google.com/", SpecificFormat.URL)
                ),
                new Pair(
                        new CreateReplicRequest("https://google.com/", "ttt", Instant.now().toString(), "blah", "dsauh/§R§QO@ſð@ŧſŋſ¢„"),
                        new EnumErrorResponse(EnumMapper.getAll(MediaMode.class), "ttt")
                ),
                new Pair(
                        new CreateReplicRequest("https://google.com/", "all", "today", "blah", "dsauh/§R§QO@ſð@ŧſŋſ¢„"),
                        new PatternErrorResponse(ValidationPatterns.INSTANT, "today")
                ),
                new Pair(
                        new CreateReplicRequest("https://google.com/", "all", Instant.now().toString(), "blah", "password_that_is_too_long_DASIASNDNISANDANDAUDSAANUSNSDSANANINDSAUSANDIASDUAISUDNAIDNAUIAUDNAI"),
                        new PatternErrorResponse(ValidationPatterns.PASSWORD, "password_that_is_too_long_DASIASNDNISANDANDAUDSAANUSNSDSANANINDSAUSANDIASDUAISUDNAIDNAUIAUDNAI")
                ),
        };
    }
}
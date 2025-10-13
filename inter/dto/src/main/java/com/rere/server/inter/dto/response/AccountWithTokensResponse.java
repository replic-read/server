package com.rere.server.inter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body with an account and auth tokens.
 */
public record AccountWithTokensResponse(
        @ValidationMetadata(FieldType.ACCOUNT_ACCOUNT) AccountResponse account,
        @ValidationMetadata(FieldType.ACCESS_TOKEN) @JsonProperty("access_token") String accessToken,
        @ValidationMetadata(FieldType.REFRESH_TOKEN) @JsonProperty("refresh_token") String refreshToken
) {

}

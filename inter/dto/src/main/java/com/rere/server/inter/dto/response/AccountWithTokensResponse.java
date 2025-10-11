package com.rere.server.inter.dto.response;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body with an account and auth tokens.
 */
public record AccountWithTokensResponse(
        @ValidationMetadata(FieldType.ACCOUNT_ACCOUNT) AccountResponse account,
        @ValidationMetadata(FieldType.ACCESS_TOKEN) String accessToken,
        @ValidationMetadata(FieldType.REFRESH_TOKEN) String refreshToken
) {

}

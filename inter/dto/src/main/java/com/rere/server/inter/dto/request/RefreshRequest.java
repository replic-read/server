package com.rere.server.inter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Request body with information of a refresh-token.
 */
public record RefreshRequest(
        @ValidationMetadata(value = FieldType.REFRESH_TOKEN, doValidate = false) @JsonProperty("refresh_token") String refreshToken
) {
}

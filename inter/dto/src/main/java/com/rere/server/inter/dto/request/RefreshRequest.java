package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Request body with information of a refresh-token.
 */
public record RefreshRequest(
        @ValidationMetadata(value = FieldType.REFRESH_TOKEN, doValidate = false) String refreshToken
) {
}

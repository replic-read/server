package com.rere.server.inter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Request body with information to update an account.
 */
public record UpdateAccountRequest(
        @ValidationMetadata(FieldType.ACCOUNT_EMAIL) String email,
        @ValidationMetadata(FieldType.ACCOUNT_USERNAME) String username,
        @ValidationMetadata(FieldType.ACCOUNT_PROFILE_COLOR) @JsonProperty("profile_color") int profileColor
) {

}

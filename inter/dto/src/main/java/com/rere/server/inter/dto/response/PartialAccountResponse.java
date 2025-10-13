package com.rere.server.inter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body with partial information about an account.
 */
public record PartialAccountResponse(
        @ValidationMetadata(FieldType.ACCOUNT_USERNAME) String username,
        @ValidationMetadata(FieldType.ACCOUNT_PROFILE_COLOR) @JsonProperty("profile_color") int profileColor,
        @ValidationMetadata(FieldType.ACCOUNT_STATE) @JsonProperty("account_state") String accountState
) {

}

package com.rere.server.inter.dto.response;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body with full information about an account.
 */
public record AccountResponse(
        @ValidationMetadata(FieldType.ACCOUNT_ID) String id,
        @ValidationMetadata(FieldType.CREATED_TIMESTAMP) String createdTimestamp,
        @ValidationMetadata(FieldType.EMAIL_TOKEN) String email,
        @ValidationMetadata(FieldType.ACCOUNT_USERNAME) String username,
        @ValidationMetadata(FieldType.ACCOUNT_PROFILE_COLOR) int profileColor,
        @ValidationMetadata(FieldType.ACCOUNT_STATE) String accountState
) {

}

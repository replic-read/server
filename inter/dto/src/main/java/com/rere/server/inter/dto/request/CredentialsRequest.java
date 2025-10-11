package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Request body with credentials.
 */
public record CredentialsRequest(
        @ValidationMetadata(value = FieldType.ACCOUNT_EMAIL, required = false) String email,
        @ValidationMetadata(value = FieldType.ACCOUNT_USERNAME, required = false) String username,
        @ValidationMetadata(FieldType.ACCOUNT_PASSWORD) String password
) {
}

package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Request body with credentials.
 * <br><br>
 * Note: Although these are fixed formats, we do not perform any validation.
 *      If in the future the formats for email/username/password get stricter,
 *      users who followed the old strictness would not be able to log in anymore.
 */
public record CredentialsRequest(
        @ValidationMetadata(value = FieldType.ACCOUNT_EMAIL, required = false, doValidate = false) String email,
        @ValidationMetadata(value = FieldType.ACCOUNT_USERNAME, required = false, doValidate = false) String username,
        @ValidationMetadata(value = FieldType.ACCOUNT_PASSWORD, doValidate = false) String password
) {
}
